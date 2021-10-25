Feature: Ranking crimes
  Scenario: Ranking crimes by crime time
    Given I add 2 new crimes with the crime time 1
    And I add 3 new crimes with the crime time 9
    And I add 4 new crimes with the crime time 12
    When I rank crimes by time
    Then the most common crime area should be "12" with frequency 4