Feature: Deleting from database
  Scenario: Deleting a crime from a database
    Given There is a valid crime in a table
    When I delete the crime
    Then The crime should be removed from the database

  Scenario: Deleting a table from a database
    Given There is a valid table in the database
    When I delete the table
    Then The table should be removed