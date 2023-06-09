package vttp2023.batch3.ssf.frontcontroller.model;

import java.util.HashMap;
import java.util.Random;

import jakarta.servlet.http.HttpSession;

public class Captcha {

    public boolean initialized;
    private String question;
    private int answer;

    public boolean getInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public Captcha(boolean initialized, String question, int answer) {
        this.initialized = initialized;
        this.question = question;
        this.answer = answer;
    }

    public static Captcha regenerateCaptcha(boolean initialized) {
        Random random = new Random();
        int num1 = random.nextInt(50) + 1;
        int num2 = random.nextInt(50) + 1;
        int opNum = random.nextInt(4);
        String question = "";
        int answer = 0;

        if (opNum == 0) {
            question = String.format("What is %s + %s?", num1, num2);
            answer = (num1 + num2);
        } 
        else if (opNum == 1) {
            question = String.format("What is %s - %s?", num1, num2);
            answer = (num1 - num2);
        } 
        else if (opNum == 2) {
            question = String.format("What is %s * %s?", num1, num2);
            answer = (num1 * num2);
        } 
        else if (opNum == 3) {
            question = String.format("What is %s / %s, rounded to nearest integer?", num1, num2);
            answer = Math.round((float) num1 / num2);
        }

        return new Captcha(initialized, question, answer);
    }

    public static Captcha retrieveCaptcha(HttpSession session, String username) {

		HashMap<String, Captcha> usernameToCaptcha = (HashMap<String, Captcha>) session.getAttribute("captcha");

		if (usernameToCaptcha == null) {
			usernameToCaptcha = new HashMap<String, Captcha>();
			return null;
		} 
        else {
			return usernameToCaptcha.get(username);
		}
	}

    public static boolean captchaCorrectAnswer(Captcha captcha, String answer) {

        if (captcha != null) {
            if (answer == null) {
                return false;
            }
            if (answer.trim().length() == 0) {
                return false;
            }
            int captchaResponseInt = Integer.parseInt(answer.trim());
            return captchaResponseInt == captcha.getAnswer();
        } 
        else {
            return true;
        }
    }

    public static void setCaptcha(HttpSession session, Captcha captcha, String username) {

		HashMap<String, Captcha> userCaptchaMap = (HashMap<String, Captcha>) session.getAttribute("captcha");

        if (userCaptchaMap == null) {
            userCaptchaMap = new HashMap<String, Captcha>();
        }

        userCaptchaMap.put(username, captcha);
        session.setAttribute("captcha", userCaptchaMap);
    }
}
