/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.spec.testsuite.authz;

import static org.fcrepo.spec.testsuite.Constants.RDF_SOURCE_LINK_HEADER;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author awoods
 * @author dbernstein
 * @since 2018-07-15
 */
public class WebACRdfSources extends AbstractAuthzTest {

    /**
     * Constructor
     *
     * @param adminUser     admin username
     * @param adminPassword admin password
     */
    @Parameters({"param2", "param3"})
    public WebACRdfSources(final String adminUser, final String adminPassword) {
        super(adminUser, adminPassword, null, null);
    }

    /**
     * 5.1 - ACL is LDPRS
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void aclIsLDPRS(final String uri) {
        final TestInfo info = setupTest("5.1",
                                        "An ACL for a controlled resource on a conforming server must itself be an " +
                                        "LDP-RS.",
                                        "https://fedora.info/2018/06/25/spec/#solid-ldp-acls", ps);

        //attempt to create a LDP-NR at an acl location and confirm that it fails.
        //create resource
        final String resourceUri = createResource(uri, info.getId());
        //get acl handle
        final String aclUri = getAclLocation(resourceUri);
        //create non-rdf acl
        final Response response = doPutUnverified(aclUri, new Headers(new Header("Content-Type", "text/plain")),
                                                  "test");
        response.then().statusCode(clientErrorRange());

        //successfully create an rdf acl
        final String aclUri2 = createAclForResource(resourceUri, "user-read-only.ttl", "anyuser");
        confirmPresenceOfLinkValue(RDF_SOURCE_LINK_HEADER, doHead(aclUri2));
    }

}
