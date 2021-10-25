Feature: Ranking crimes
  Scenario: Ranking crimes by crime type
    Given I add a new crime with the crime type "Theft"
    When I rank crimes
    Then the most common crime should be "Theft" with frequency value 1