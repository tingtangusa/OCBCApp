Feature: Make appointment with PFC

  Scenario: As a valid user I can make a PFC appointment at a branch
    Given I have already setup
      Then I touch the "APPTS" text
      Then I fill up make appointment form with branch as "Clementi" and date as "05-05-2013"
      Then I wait for dialog to close
      Then I toggle checkbox number 1
      Then I toggle checkbox number 3
      Then I press view with id "submitButton"
      Then I wait for progress
      Then I should see text containing "queue number"
