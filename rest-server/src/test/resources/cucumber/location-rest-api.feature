Feature: End to End test for location REST API endpoints with auth

  Scenario: admin user can view/add/update/delete location
    Given REST server url in system property "rest-server-url"
    Given Keycloak auth server url in system property "keycloak.url", realm "opennms" and client "admin-cli"
    Given Admin user "admin-user" with password "password123"
    Then Admin user can create an access token
    Then Admin user can create new location
    Then Admin user can list location
    Then Admin user can get location by ID
    Then Admin user can update the location
    Then Admin user can delete the location by ID

  Scenario: Normal user only can view location
    Given REST server url in system property "rest-server-url"
    Given Keycloak auth server url in system property "keycloak.url", realm "opennms" and client "admin-cli"
    Given A normal user with username "test-user" and password "password123"
    Then Normal user can login and create access token
    Then Normal user can list location
    Then Normal user can get location by ID
    Then Normal user am not allowed to create new location
    Then Normal user am not allowed to update the location by ID
    Then Normal user am not allowed to delete the location

  Scenario: Not authorized user can't access the REST API
    Given REST server url in system property "rest-server-url"
    Then Without in correct token user can't access rest api


