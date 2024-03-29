package com.google.controller;

import com.google.Frames.FramesController;
import com.google.enums.Language;
import com.google.model.Users;
import com.google.services.DatabaseService;
import com.google.templates.BotState;
import com.google.templates.Result;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class MessageController {

    public static EditMessageText getGreeting(Update update, Language language){
        Result result = getChatId(update);
        ReplyKeyboardMarkup replyKeyboardMarkup = FramesController.optionsButton(language);
        EditMessageText sendMessage = new EditMessageText();
        switch (language){
            case RUS:
                sendMessage.setText("<b>Привет</b>, этот бот поможет тебе в нескольких шагах " +
                "Вы можете узнать больше через /help\n\nОтправьте нужные изображения, нажав кнопку Генератор PDF.");
                break;
            case UZBEK:
                sendMessage.setText("<b>Salom</b> , bu bot sizga bir nechta amallarni bajarishda yordam beradi\n\n/help buyrug'ini bosish orqali qo'shimcha ma'lumot olishingiz mumkin\n\n" +
                        "<b>PDF yaratish</b> tugmasi orqali siz rasmlarni osongina pdf file ko'rinishiga keltira olasiz");
                break;

            case ENGLISH:
                sendMessage.setText("<b>Hi</b> , This bot helps you for media processing that can work with pdf documents\n\n" +
                "You can get more information with /help\n\nYou can easily make a pdf with this bot so press button <b>PDF Generator</b>");
                break;
        }
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(result.getChatId().toString());
        sendMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        return sendMessage;
    }
    public static SendMessage getGreetingWithoutUpdate(Update update, Language language){
        Result result = getChatId(update);
        ReplyKeyboardMarkup replyKeyboardMarkup = FramesController.optionsButton(language);
        SendMessage sendMessage = new SendMessage();
        switch (language){
            case RUS:
                sendMessage.setText("<b>Привет</b> Этот бот поможет тебе в нескольких шагах " +
                        "Вы можете узнать больше через /help\n\nОтправьте нужные изображения, нажав кнопку Генератор PDF.");
                sendMessage.setReplyMarkup(FramesController.optionsButton(Language.RUS));

                break;
            case UZBEK:
                sendMessage.setText("<b>Salom</b> Bu bot sizga bir nechta amallarni bajarishda yordam beradi\n\n/help buyrug'ini bosish orqali qo'shimcha ma'lumot olishingiz mumkin\n\n" +
                        "<b>PDF yaratish</b> tugmasi orqali siz rasmlarni osongina pdf file ko'rinishiga keltira olasiz");
                sendMessage.setReplyMarkup(FramesController.optionsButton(Language.UZBEK));

                break;

            case ENGLISH:
                sendMessage.setText("<b>Hi</b> This bot helps you for media processing that can work with pdf documents\n\n" +
                        "You can get more information with /help\n\nYou can easily make a pdf with this bot so press button <b>PDF Generator</b>");
                sendMessage.setReplyMarkup(FramesController.optionsButton(Language.ENGLISH));

                break;
        }
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setChatId(result.getChatId().toString());
        DatabaseService.setBotState(update,BotState.GETPHOTO);
        return sendMessage;
    }
    public static SendMessage chooseLang(Update update){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        StringBuilder stringBuilder = new StringBuilder();
        String firstName = update.getMessage().getFrom().getFirstName();
        stringBuilder.append("Salom <b>"+firstName+"</b> Til tanlang \uD83C\uDDFA\uD83C\uDDFF").append(System.lineSeparator()).append("Привет <b>"+firstName+"</b> Выберите свой язык \uD83C\uDDF7\uD83C\uDDFA")
                .append(System.lineSeparator()).append("Helllo <b>"+firstName+"</b> Choose your language \uD83C\uDDFA\uD83C\uDDF8");
        sendMessage.setText(stringBuilder.toString());
        sendMessage.setReplyMarkup(FramesController.makeButtonLanguage());
        sendMessage.setParseMode(ParseMode.HTML);

        return sendMessage;
    }
    public static Result getChatId(Update update){
        if (update.hasMessage()) return new Result(update.getMessage().getChatId(),"message");
        else return  new Result(update.getCallbackQuery().getMessage().getChatId(),"callback");

    }
    public static SendMessage getStart(Update update){
        Users users = new Users();
        users.setUserName(update.getMessage().getFrom().getUserName());
        users.setChatId(update.getMessage().getChatId());
        users.setBotState(BotState.GETLANG);
        DatabaseService.saveUsers(users);
        return MessageController.chooseLang(update);
    }
    public static SendMessage getOptionsKeyboard(Update update,Language language){
        System.out.println(language);
        Result chatId = getChatId(update);
        ReplyKeyboardMarkup replyKeyboardMarkup = FramesController.optionsButton(language);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.getChatId().toString());
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        if (language.equals(Language.UZBEK))
            sendMessage.setText("Tanlang \uD83D\uDC47");
        else if (language.equals(Language.RUS))
            sendMessage.setText("Выбирать \uD83D\uDC47");
            else sendMessage.setText("Choose \uD83D\uDC47");
        return sendMessage;
    }

    public static SendMessage stikerBot(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("\uD83D\uDDC2");
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        return sendMessage;
    }



    public static SendMessage askPhoto(Update update) {
        Result result = MessageController.getChatId(update);
        SendMessage sendMessage = new SendMessage();
        boolean b = DatabaseService.setBotState(update, BotState.GETPHOTO);
        sendMessage.setChatId(result.getChatId().toString());
        if (!b){
            Language userLanguage = DatabaseService.getUserLanguage(update);
            switch(userLanguage){
                case UZBEK:
                    sendMessage.setText("Iltimos PDF generatsiya Qilish uchun rasm yuboring \uD83D\uDCE4");
                    break;
                case ENGLISH:
                    sendMessage.setText("Please send an image to make a PDF generation \uD83D\uDCE4");
                    break;
                case RUS:
                    sendMessage.setText("Отправьте изображение, чтобы создать PDF-файл \uD83D\uDCE4");
                    break;
            }
            return sendMessage;

        }else {
            sendMessage.setText("Xatolik yuz berdi \uD83D\uDE2C iltimos /start buyrug'ini qayta jo'nating");
            return sendMessage;
        }
    }

    public static SendMessage maxFileSizeOutOfMessage(Update update,Language language) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(language.name().equals(Language.ENGLISH.name())?"Max file size has exceeded,You can sen less than 10 MB":language.name().equals(Language.UZBEK.name())?"Maksimal fayl xajmi chegaradan oshib ketti , Siz faqatgina 10MB dan kam filelarni yuklay olasiz":"Превышен максимальный размер файла, вы можете загружать только файлы размером менее 10 МБ");
        sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
        return sendMessage;

    }

    public static SendMessage notAllowedFileFormat(Update update, Language userLanguage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(userLanguage.name().equals(Language.ENGLISH.name())?"File format not allowed ❌":userLanguage.name().equals(Language.UZBEK.name())?"Faylingiz xato formatda iltimos file rasm ekanligiga ishonch hosil qiling ❌":"Пожалуйста, убедитесь, что ваш файл имеет неправильный формат файла изображения ❌");
        sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
        return sendMessage;
    }

    public static EditMessageText editSendedFile(Update update, Language userLanguage) {
        Integer messageId;
        if (update.hasMessage()){
            messageId = update.getMessage().getMessageId();
        }else {
            messageId = update.getCallbackQuery().getMessage().getMessageId();
        }
        Result chatId = MessageController.getChatId(update);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId.getChatId().toString());
        editMessageText.setMessageId(messageId);
        editMessageText.setText(userLanguage.name().equals(Language.ENGLISH.name())?"Your file in a processing ♻️":userLanguage.name().equals(Language.UZBEK.name())?
                "Sizning faylingizga ishlov berilmoqda ♻️":"Ваш файл находится в обработке ♻️");
        return editMessageText;
    }

    public static SendMessage newUser(Update update) {
        User from = update.getMessage().getFrom();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId("968877318");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("name : "+from.getUserName()).append("\n").append("chat id"+from.getId()).append("\n")
                .append("username : "+from.getUserName());

        sendMessage.setText(stringBuilder.toString());
        return sendMessage;
    }

    public static SendMessage sendAnimationStiker(Update update) {
        Result res = getChatId(update);
        SendMessage sendMessage = new SendMessage(res.getChatId().toString(),"\uD83D\uDCDD");
        return sendMessage;
    }
}
