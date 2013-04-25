Feature: See nearest branches around the user

  Scenario: As a valid user I can see the branches near me
    Given I have already setup
      Then I touch the "NEAREST BRANCHES" text
      Then I wait up to 5 seconds to see "km"
      Then I should see text containing "km"
