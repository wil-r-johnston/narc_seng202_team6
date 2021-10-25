Feature: Ranking crimes
  Scenario: Ranking crimes by crime area
    Given I add 3 new crimes with the crime area "077XX S NORTH SHORE RD"
    And I add 2 new crimes with the crime area "062XX P ILAM RD"
    When I rank crimes by area
    Then the most common crime area should be "077" with frequency 3 followed by "062" with frequency 2