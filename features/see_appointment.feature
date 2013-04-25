Feature: See appointment that was made

  Scenario: As a valid user I can see appointments that I have made
    Given I have already setup
      Given I have made an appointment
        Then I click on the option menu
        Then I press "My Appointments"
        Then I should see "06-06-2013"
        Then I should see "Clementi"
