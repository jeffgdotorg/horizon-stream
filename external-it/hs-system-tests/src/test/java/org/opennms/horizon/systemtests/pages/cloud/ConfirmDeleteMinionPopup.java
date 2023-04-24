package org.opennms.horizon.systemtests.pages.cloud;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ConfirmDeleteMinionPopup {
    private final static SelenideElement deleteBtn = $("[data-testid='save-btn']");

    public static void clickDeleteBtn() {
        deleteBtn.shouldBe(Condition.enabled).click();
    }

}
