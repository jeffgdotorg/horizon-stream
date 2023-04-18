/*******************************************************************************
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
 *******************************************************************************/

package org.opennms.horizon.systemtests.keyvalue;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

public class SecretsStorage {
    public static final SecretClient secretClient = new SecretClientBuilder()
        .vaultUrl("https://automation-test-vault.vault.azure.net/")
        .credential(new DefaultAzureCredentialBuilder().build())
        .buildClient();

    public static String portalHost = getSecretValueForEnv("portal-host");
    public static String adminUserEmail = getSecretValueForEnv("hs-portal-userEmail");
    public static String adminUserPassword = getSecretValueForEnv("hs-portal-userPassword");
    public static String oktaUserEmail = getSecretValueForEnv("hs-portal-oktaEmail");

    private static String getSecretValueForEnv(String secret) {
        return secretClient.getSecret(secret + "-" + "dev").getValue();
    }

    private static String getSecretValue(String secret) {
        return secretClient.getSecret(secret).getValue();
    }
}
