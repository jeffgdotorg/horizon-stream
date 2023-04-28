/*
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2023 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2023 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *
 */

package org.opennms.horizon.minion.grpc.ssl;

import com.google.common.base.Strings;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.ApplicationProtocolConfig;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import java.security.GeneralSecurityException;
import javax.net.ssl.TrustManagerFactory;
import lombok.Setter;

import javax.net.ssl.KeyManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MinionGrpcSslContextBuilderFactoryImpl implements MinionGrpcSslContextBuilderFactory {

    @Setter
    private String keystore;
    @Setter
    private String keystoreType;
    @Setter
    private String keystorePassword;
    @Setter
    private String truststore;
    @Setter
    private String truststoreType;
    @Setter
    private String truststorePassword;

    @Setter
    private KeyManagerFactory keyManagerFactory;

    @Setter
    private TrustManagerFactory trustManagerFactory;

    @Setter
    private KeyStoreFactory keyStoreFactory;

    @Setter
    private Supplier<SslContextBuilder> grpcSslClientContextFactory = GrpcSslContexts::forClient;


    @Override
    public SslContextBuilder create() throws GeneralSecurityException {
        SslContextBuilder builder = grpcSslClientContextFactory.get();

        if (isSet(truststore)) {
            File trustStoreFile = new File(truststore.trim());
            if (trustStoreFile.exists()) {
                try {
                    KeyStore keyStore = keyStoreFactory.createKeyStore(truststoreType, trustStoreFile, truststorePassword);
                    TrustManagerFactory trustManagerFactory = trustManager();
                    trustManagerFactory.init(keyStore);
                    builder.trustManager(trustManagerFactory);
                } catch (Exception exc) {
                    throw new RuntimeException("Failed to configure trust store for GRPC connection", exc);
                }
            } else {
                throw new RuntimeException("Configured trust store" + trustStoreFile.getAbsolutePath() + " does not exist");
            }
        }

        if (isSet(keystore)) {
            File keyStoreFile = new File(keystore.trim());
            if (keyStoreFile.exists()) {
                try {
                    configureKeyManagerPkcs12(builder);
                } catch (Exception exc) {
                    throw new RuntimeException("Failed to initialize client key manager", exc);
                }
            } else {
                throw new RuntimeException("Configured keystore " + keyStoreFile.getAbsolutePath() + " does not exist.");
            }
        }

        return builder;
    }

    private boolean isSet(String value) {
        return value != null && !value.isBlank();
    }

//========================================
// Internals
//----------------------------------------

    private void configureKeyManagerPkcs12(SslContextBuilder builder) throws GeneralSecurityException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = keyStoreFactory.createKeyStore(keystoreType, new File(keystore), keystorePassword);
        KeyManagerFactory keyManagerFactory = keyManager();
        keyManagerFactory.init(keyStore, null); // this is password for private key password

        ApplicationProtocolConfig apn =
            new ApplicationProtocolConfig(
                ApplicationProtocolConfig.Protocol.ALPN,
                ApplicationProtocolConfig.SelectorFailureBehavior.FATAL_ALERT,
                ApplicationProtocolConfig.SelectedListenerFailureBehavior.FATAL_ALERT,
                "h2"
            );

        builder
            .keyManager(keyManagerFactory)
            .sslProvider(SslProvider.JDK)
            .applicationProtocolConfig(apn)
            ;
    }

    private char[] passwordAsCharArray(String password) {
        if (password != null && !password.isBlank()) {
            return password.toCharArray();
        }

        return null;
    }

    private TrustManagerFactory trustManager() throws NoSuchAlgorithmException {
        if (trustManagerFactory == null) {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        }
        return trustManagerFactory;
    }

    private KeyManagerFactory keyManager() throws NoSuchAlgorithmException {
        if (keyManagerFactory == null) {
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        }
        return keyManagerFactory;
    }

//========================================
// Functional Interface
//  with Exception Declaration
//----------------------------------------

    public interface FunctionWithException<T,R,E extends Exception> {
        R apply(T arg) throws E;
    }
}
