Feature: Inserting into database
  Scenario: Adding a crime to the database
    Given There is a valid table in the database
    When I add a new crime with valid crime fields
    Then The crime should be added to the database

  Scenario: Adding an invalid crime to the database
    Given There is a valid table in the database
    When I add a duplicate crime to the table
    Then The crime should not be added to the database