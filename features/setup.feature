Feature: Setup feature

  Scenario: As a valid user I can perform setup on my app
    Given I see the text "Welcome"
      Then I fill up form with my name as "Jason", id type as "NRIC", id as "S9546551B", mobile as "96191234"
      Then I wait for progress
    Given I should see text containing "Successfully Setup"
      Then I press the "OK" button
