Feature: See waiting time of branches

  Scenario: As a valid user I can see waiting times of branches
    Given I have already setup
      Then I press button number 2
      Then I wait up to 5 seconds to see "mins"
      Then I should see text containing "mins"
