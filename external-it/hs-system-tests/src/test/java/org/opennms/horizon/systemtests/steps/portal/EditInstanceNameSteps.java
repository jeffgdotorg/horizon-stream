package org.opennms.horizon.systemtests.steps.portal;

import io.cucumber.java.en.Then;
import org.opennms.horizon.systemtests.pages.portal.EditInstanceNamePopup;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disappear;

public class EditInstanceNameSteps {
    @Then("set new name {string} in 'Instance Name' field")
    public void setNewInstanceName(String instanceName) {
        EditInstanceNamePopup.setNewInstanceName(instanceName);
    }

    @Then("click on 'UPDATE' button")
    public void clickUpdateBtn() {
        EditInstanceNamePopup.clickUpdateBtn();
    }

    @Then("click on 'CANCEL' button to close 'Edit Instance' popup")
    public void clickCancelBtn() {
        EditInstanceNamePopup.clickCancelBtn();
        EditInstanceNamePopup.waitNewState(disappear);
    }

    @Then("click on 'X' button to close 'Edit Instance' popup")
    public void clickCloseBtn() {
        EditInstanceNamePopup.clickCloseBtn();
        EditInstanceNamePopup.waitNewState(disappear);
    }

    @Then("'Edit Instance' popup appears")
    public void waitPopup() {
        EditInstanceNamePopup.waitNewState(appear);
    }

    @Then("'Edit Instance' popup disappear")
    public void waitPopupDisappear() {
        EditInstanceNamePopup.waitNewState(disappear);
    }

    @Then("'Edit Instance' popup shows an error message {string} for Instance name")
    public void verifyErrorMessage(String errorMessage) {
        EditInstanceNamePopup.verifyErrorMessage(errorMessage);
    }
}
