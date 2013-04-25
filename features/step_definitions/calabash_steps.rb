require 'calabash-android/calabash_steps'

Then %{I fill up setup form with my name as "$name", id type as "$id_type", id as "$id", mobile as "$mobile"} do |name, id_type, id, mobile|
  steps %{
    Then I enter "#{name}" into "Full Name"
    And I select "#{id_type}" from "ID Type"
    And I enter text "#{id}" into field with id "custId"
  	And I enter "#{mobile}" into "Mobile Number"
  	And I press the "OK" button
  }
end

Given %{I have already setup} do
	steps %{
      Given I see the text "Welcome"
        Then I fill up setup form with my name as "Jason", id type as "NRIC", id as "S9546551B", mobile as "96191234"
        Then I wait for progress
        Given I should see text containing "Successfully Setup"
        Then I press the "OK" button
	}
end

Then %{I fill up make appointment form with branch as "$branch" and date as "$date"} do |branch, date|
  steps %{
    Then I select "#{branch}" from "Branch"
    Then I press view with id "dateSpinner"
    Then I set the date to "#{date}" on DatePicker with index "1"
    Then I press the "Done" button
  }
end

Given %{I have made an appointment} do
  steps %{
    Then I touch the "APPTS" text
    Then I fill up make appointment form with branch as "Clementi" and date as "06-06-2013"
    Then I wait for dialog to close
    Then I toggle checkbox number 1
    Then I toggle checkbox number 3
    Then I press view with id "submitButton"
    Then I wait for progress
    Then I should see text containing "queue number"
    Then I press the "Got it!" button
  }
end

Then %{I click on the option menu} do
  steps %{
    Then I click on screen 90% from the left and 10% from the top
  }
end
