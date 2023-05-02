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

import io.grpc.netty.shaded.io.netty.handler.ssl.ApplicationProtocolConfig;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import javax.net.ssl.TrustManagerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;
import org.opennms.horizon.minion.grpc.ssl.MinionGrpcSslContextBuilderFactoryImpl.FunctionWithException;

import javax.net.ssl.KeyManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.function.Supplier;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class MinionGrpcSslContextBuilderFactoryImplTest {

    private MinionGrpcSslContextBuilderFactoryImpl target;

    private Supplier<SslContextBuilder> mockGrpcSslClientContextFactory;
    private SslContextBuilder mockSslContextBuilder;

    private FunctionWithException<String, KeyStore, KeyStoreException> mockKeyStoreFactory;
    private KeyStore mockKeyStore;

    private FunctionWithException<String, FileInputStream, IOException> mockFileInputStreamFactory;
    private FileInputStream mockPkcs12FileInputStream;

    private FunctionWithException<String, KeyManagerFactory, NoSuchAlgorithmException> mockKeyManagerFactoryFactory;

    @Mock
    private TrustManagerFactory trustManagerFactory;
    @Mock
    private KeyManagerFactory mockKeyManagerFactory;
    @Mock
    private KeyStoreFactory keyStoreFactory;

    @Mock
    private KeyStore keyStore;

    @BeforeEach
    public void setUp() throws Exception {
        mockGrpcSslClientContextFactory = Mockito.mock(Supplier.class);
        mockSslContextBuilder = Mockito.mock(SslContextBuilder.class);

//        Mockito.when(mockGrpcSslClientContextFactory.get()).thenReturn(mockSslContextBuilder);
//        Mockito.when(mockKeyStoreFactory.apply("pkcs12")).thenReturn(mockKeyStore);
//        Mockito.when(mockFileInputStreamFactory.apply("x-client-key-path")).thenReturn(mockPkcs12FileInputStream);
//        Mockito.when(mockKeyManagerFactoryFactory.apply(KeyManagerFactory.getDefaultAlgorithm())).thenReturn(mockKeyManagerFactory);

        target = new MinionGrpcSslContextBuilderFactoryImpl();
        target.setKeyStoreFactory(keyStoreFactory);
        target.setKeyManagerFactory(mockKeyManagerFactory);
        target.setTrustManagerFactory(trustManagerFactory);

        target.setGrpcSslClientContextFactory(mockGrpcSslClientContextFactory);
    }

    @Test
    void testCreateEmpty() throws Exception {
        //
        // Execute
        //
        SslContextBuilder result = target.create();

        //
        // Verify the Results
        //
        assertSame(result, mockSslContextBuilder);
        Mockito.verifyNoInteractions(mockSslContextBuilder);
    }

    @Test
    void testInvalidTrustStore() throws Exception {
        target.setTruststore("invalid");
        Assertions.assertThrows(RuntimeException.class, target::create);

        target.setTruststore("   ");
        Assertions.assertDoesNotThrow(target::create);
    }

    @Test
    void testInvalidCertificateOrKey() throws Exception {
        target.setKeystore("invalid");
        target.setTruststore("invalid");
        Assertions.assertThrows(RuntimeException.class, target::create);

        target.setKeystore("   ");
        target.setTruststore("   ");
        Assertions.assertDoesNotThrow(target::create);

        target.setKeystore("invalid");
        target.setTruststore("   ");
        Assertions.assertThrows(RuntimeException.class, target::create);

        target.setKeystore("   ");
        target.setTruststore("invalid");
        Assertions.assertThrows(RuntimeException.class, target::create);
    }

    @Test
    void testCreatePEM(@TempDir File root) throws Exception {
        commonTestCreatePEM(root, null);
    }

    @Test
    void testCreatePKCS12(@TempDir File root) throws Exception {
        commonTestCreatePkcs12(root);
    }

    @Test
    void testPasswordNotNullPkcs12(@TempDir File root) throws Exception {
        target.setKeystorePassword("x-password-x");
        commonTestCreatePkcs12(root);
    }

    @Test
    void testPasswordNotNullPEM(@TempDir File root) throws Exception {
        target.setKeystorePassword("x-password-x");
        commonTestCreatePEM(root, "x-password-x");
    }

    @Test
    public void testConfigureKeyManagerException(@TempDir File root) {
        //
        // Setup Test Data and Interactions
        //
        var testException = new RuntimeException("x-text-exc-x");
        File clientCert = new File(root, "x-client-keystore");
        File clientKey = new File(root, "x-client-key-path");
        Mockito.when(mockSslContextBuilder.keyManager(clientCert, clientKey, null)).thenThrow(testException);

        //
        // Execute
        //
        target.setKeystore(clientCert.getAbsolutePath());
        target.setKeystoreType("file");
//        target.setTrustCertCollectionFilePath("x-truststore");

        Exception actualException = null;

        try {
            SslContextBuilder result = target.create();
            fail("missing expected exception");
        } catch (Exception exc) {
            actualException = exc;
        }

        //
        // Verify the Results
        //
        assertTrue(actualException.getMessage().contains("x-truststore does not exist"));
    }

//========================================
// Internals
//----------------------------------------

    void commonTestCreatePEM(File root, String expectedPassword) throws Exception {
        //
        // Setup Test Data and Interactions
        //
        var keyStore = new File(root, "x-client-keystore");
        var trustStore = new File(root, "x-truststore");
        KeyStoreWrapper.create(keyStore, null);
        KeyStoreWrapper.create(trustStore, null);

        //
        // Execute
        //
//        target.setClientCertChainFilePath(keyStore.getAbsolutePath());
//        target.setClientPrivateKeyFilePath(clientKey.getAbsolutePath());
//        target.setClientPrivateKeyIsPkcs12(false);
//        target.setTrustCertCollectionFilePath(trustStore.getAbsolutePath());

        SslContextBuilder result = target.create();

        //
        // Verify the Results
        //
        assertSame(result, mockSslContextBuilder);
        Mockito.verify(mockSslContextBuilder).trustManager(any(TrustManagerFactory.class));
        Mockito.verify(mockSslContextBuilder).keyManager(any(KeyManagerFactory.class));
    }

    private void commonTestCreatePkcs12(File root) throws Exception {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(mockSslContextBuilder.keyManager(any(KeyManagerFactory.class))).thenReturn(mockSslContextBuilder);
        Mockito.when(mockSslContextBuilder.sslProvider(SslProvider.JDK)).thenReturn(mockSslContextBuilder);
        Mockito.when(mockSslContextBuilder.applicationProtocolConfig(any(ApplicationProtocolConfig.class))).thenReturn(mockSslContextBuilder);

        //
        // Execute
        //
        File clientCert = new File(root, "x-client-keystore");
        KeyStoreWrapper.create(clientCert, "");
        File clientKey = new File(root, "x-client-key-path");
        clientKey.createNewFile();
        File trustCert = new File(root, "x-truststore");
        KeyStoreWrapper.create(trustCert, null);
        trustCert.createNewFile();
//        target.setClientCertChainFilePath(clientCert.getAbsolutePath());
//        target.setClientPrivateKeyFilePath(clientKey.getAbsolutePath());
//        target.setClientPrivateKeyIsPkcs12(true);
//        target.setTrustCertCollectionFilePath(trustCert.getAbsolutePath());
//        target.setKeyStoreFactory(mockKeyStoreFactory);
//        target.setFileInputStreamFactory(mockFileInputStreamFactory);
//        target.setKeyManagerFactoryFactory(mockKeyManagerFactoryFactory);

        SslContextBuilder result = target.create();

        //
        // Verify the Results
        //
        var matcher =
            makeApplicationProtocolConfigMatcher(
                ApplicationProtocolConfig.Protocol.ALPN,
                ApplicationProtocolConfig.SelectorFailureBehavior.FATAL_ALERT,
                ApplicationProtocolConfig.SelectedListenerFailureBehavior.FATAL_ALERT,
                "h2");

        assertSame(result, mockSslContextBuilder);
        Mockito.verify(mockSslContextBuilder).trustManager(trustCert);
        Mockito.verify(mockSslContextBuilder).keyManager(mockKeyManagerFactory);
        Mockito.verify(mockSslContextBuilder).applicationProtocolConfig(Mockito.argThat(matcher));
    }

    private ArgumentMatcher<ApplicationProtocolConfig>
    makeApplicationProtocolConfigMatcher(
        ApplicationProtocolConfig.Protocol protocol,
        ApplicationProtocolConfig.SelectorFailureBehavior selectorBehavior,
        ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedBehavior,
        String... supportedProtocols) {

        return argument -> {
            if (
                (argument.protocol() == protocol) &&
                (argument.selectorFailureBehavior() == selectorBehavior) &&
                (argument.selectedListenerFailureBehavior() == selectedBehavior) &&
                (argument.supportedProtocols().size() == supportedProtocols.length)) {

                int cur = 0;
                while (cur < supportedProtocols.length) {
                    if (!Objects.equals(supportedProtocols[cur], argument.supportedProtocols().get(cur))) {
                        return false;
                    }
                    cur++;
                }

                return true;
            }

            return false;
        };
    }

    static class KeyStoreWrapper {

        public static KeyStore create(File file, String password) throws IOException, GeneralSecurityException {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.store(new FileOutputStream(file), password == null ? null : password.toCharArray());
            return keyStore;
        }
    }
}
