package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;

class DeliveryTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful book and rebook meeting")
    void shouldSuccessfulBookAndRebookMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        int daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $("[placeholder='Город']").setValue(validUser.getCity());
        $("[placeholder='Дата встречи']").sendKeys(Keys.chord(Keys.SHIFT, Keys.UP), Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(firstMeetingDate);
        $("[name='name']").setValue(validUser.getName());
        $("[name='phone']").setValue(validUser.getPhone());
        $(".checkbox__box").click();
        $$("button").find(Condition.exactText("Забронировать")).click();


        if ($(withText("Доставка в выбранный город недоступна")).isDisplayed()) {
            int i = validUser.getCity().length() - 2;
            while (i > 0) {
                $("[data-test-id=city] .input__control").sendKeys(Keys.BACK_SPACE);
                i--;
            }
            $(".menu-item__control").click();
            $(".button__text").click();
        }
        $("div.notification__content").shouldHave(Condition.exactText("Встреча успешно забронирована на " + firstMeetingDate));
        $("[placeholder='Дата встречи']").sendKeys(Keys.chord(Keys.SHIFT, Keys.UP), Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(secondMeetingDate);
        $$("button").find(Condition.exactText("Забронировать")).click();
        $(withText("Необходимо подтверждение")).shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("div.notification__content > button").click();
        $("div.notification__content").shouldHave(Condition.exactText("Встреча успешно забронирована на " + secondMeetingDate));
    }

}
