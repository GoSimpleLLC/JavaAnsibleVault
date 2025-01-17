/*
 * Copyright 2016 - Fabio "MrWHO" Torchetti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wedjaa.ansible.vault.crypto;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


import static org.junit.Assert.*;


public class VaultHandlerTest
{

    final static String TEST_STRING = "This is a test";
    final static String TEST_PASSWORD = "password";
    final static String TEST_WRONG_PASSWORD = "not_this_one";
    final static String WRONG_PASS_EX = "HMAC Digest doesn't match - possibly it's the wrong password.";
    final static String DECODED_VAULT =
        "!net.wedjaa.ansible.vault.ProvisioningInfo\n"
      + "apiClientId: The provisioner ClientId\n"
      + "apiPassword: The secret password\n"
      + "apiUser: Secret User\n";

    Logger logger = LoggerFactory.getLogger(VaultHandlerTest.class);

    @Test
    public void testByteArrayValidVault()
    {
        logger.info("Testing Byte Array decryption - Valid Password");
        try
        {
            byte [] encryptedTest = VaultHandler.encrypt(TEST_STRING.getBytes(), TEST_PASSWORD);
            logger.debug("Encrypted vault:\n{}", new String(encryptedTest));
            byte [] decryptedTest = VaultHandler.decrypt(encryptedTest, TEST_PASSWORD);
            logger.debug("Decrypted vault:\n{}", new String(decryptedTest));
            assertEquals(TEST_STRING, new String(decryptedTest));

        } catch(Exception ex) {
            fail("Failed to decode the test vault: " + ex.getMessage());
        }
    }

    @Test
    public void testByteArrayInvalidVault()
    {
        logger.info("Testing Byte Array decryption - Invalid Password");
        try
        {
            byte [] encryptedTest = VaultHandler.encrypt(TEST_STRING.getBytes(), TEST_PASSWORD);
            logger.debug("Encrypted vault:\n{}", new String(encryptedTest));
            byte [] decryptedTest = VaultHandler.decrypt(encryptedTest, TEST_WRONG_PASSWORD);
            logger.debug("Decrypted vault:\n{}", new String(decryptedTest));
            fail("Should not be able to decrypt text with the wrong password");
        } catch(Exception ex) {
            assertEquals(WRONG_PASS_EX, ex.getMessage());
        }
    }

    @Test
    public void testStreamValidVault()
    {
        logger.info("Testing decoding vault Stream - Valid password ");
        try
        {
            ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
            InputStream encodedStream = getClass().getClassLoader().getResourceAsStream("test-vault.yml");
            VaultHandler.decrypt(encodedStream, decodedStream, TEST_PASSWORD);
            String decoded = new String(decodedStream.toByteArray());
            assertEquals(DECODED_VAULT, decoded);
        } catch(Exception ex) {
            fail("Failed to decode the test vault from stream: " + ex.getMessage());
        }
    }

    @Test
    public void testStreamInvalidVault()
    {
        logger.info("Testing decoding vault Stream - Invalid password ");
        try
        {
            ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
            InputStream encodedStream = getClass().getClassLoader().getResourceAsStream("test-vault.yml");
            VaultHandler.decrypt(encodedStream, decodedStream, TEST_WRONG_PASSWORD);
            String decoded = new String(decodedStream.toByteArray());
            fail("Should not be able to decrypt text with the wrong password");

        } catch(Exception ex) {
            assertEquals(WRONG_PASS_EX, ex.getMessage());
        }
    }

    @Test
    public void testStreamValidVaultLF()
    {
        logger.info("Testing decoding vault Stream with lf - Valid password ");
        try
        {
            ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
            InputStream encodedStream = getClass().getClassLoader().getResourceAsStream("test-vault-lf.yml");
            VaultHandler.decrypt(encodedStream, decodedStream, TEST_PASSWORD);
            String decoded = new String(decodedStream.toByteArray());
            assertEquals(DECODED_VAULT, decoded);
        } catch(Exception ex) {
            fail("Failed to decode the test vault from stream: " + ex.getMessage());
        }
    }

    @Test
    public void testStreamValidVaultCR()
    {
        logger.info("Testing decoding vault Stream with cr - Valid password ");
        try
        {
            ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
            InputStream encodedStream = getClass().getClassLoader().getResourceAsStream("test-vault-cr.yml");
            VaultHandler.decrypt(encodedStream, decodedStream, TEST_PASSWORD);
            String decoded = new String(decodedStream.toByteArray());
            assertEquals(DECODED_VAULT, decoded);
        } catch(Exception ex) {
            fail("Failed to decode the test vault from stream: " + ex.getMessage());
        }
    }

}
