@cloud
Feature: NetFlows basic function

  Scenario: User sees empty 'Flows' chart before data was sent
    Given No netflow data was sent
    And sees "No applications data was found in last 24 hours.." subtitle in the 'Top 10 Applications' chart
    Then click on 'Flows' link
    And sees 'No Data' in the flows table

  Scenario: User sees flows data sent to the instance
    Given Navigate to the "locations" through the left panel
    Given location "Location Flow Testing" created
    Then click on 'Download Certificate' button, get certificate and password for minion "minion_test_id_2" and start minion using "docker-compose-cloud.yaml"
    Then check "Location Flow Testing" location exists
    Then click on location "Location Flow Testing"
    Then check minion "minion_test_id_2" exists
    Then Navigate to the "discovery" through the left panel
    Then add SNMP discovery "Flows Testing Discovery" for location "Location Flow Testing" with IP "172.31.10.10"
    Then Navigate to the "inventory" through the left panel
    Then check node with system name "automated-testing-sysName" discovered successfully
    Then Navigate to the "" through the left panel
    And wait until the 'Top 10 Applications' chart will reflect the received data
    Then click on 'Flows' link
    And sees chart for netflow data
    Then click on 'Exporter' filter
    And check if exporter "automated-testing-sysName" visible in the dropdown
