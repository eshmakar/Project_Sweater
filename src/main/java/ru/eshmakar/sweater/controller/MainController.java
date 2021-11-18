package ru.eshmakar.sweater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.eshmakar.sweater.domain.Message;
import ru.eshmakar.sweater.domain.User;
import ru.eshmakar.sweater.repos.MessageRepo;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private MessageRepo messageRepo;

    @Value("${upload.path}") //из application.context берем ссылку на адрес папки
    private String uploadPath; //и сохраняем это в переменную

    @GetMapping("/")//приветствие
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main") //по умолчанию выводит все сообщения
    public String main(Model model, @RequestParam(required = false, defaultValue = "") String filter) {
        Iterable<Message> allMessages;

        if (filter != null && !filter.isEmpty())
            allMessages = messageRepo.findByTag(filter);
        else allMessages = messageRepo.findAll();

        model.addAttribute("messages", allMessages);
        model.addAttribute("filter", filter);
        return "main";
    }

    @PostMapping("/main") //добавляем сообщение
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag, Map<String, Object> model,
            @RequestParam("file") MultipartFile file //Представление загруженного файла
    ) throws IOException {
        Message message = new Message(text, tag, user);

        if (file != null && !file.getOriginalFilename().isEmpty()){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists())
                uploadDir.mkdirs(); //если не работает, тогда менять на mkdirs()

            String uuidFile = UUID.randomUUID().toString(); //чтобы исключать коллизии, генерируем рандомные значения и
            String resultFilename = uuidFile + "."+file.getOriginalFilename();//и добавляем это имя перед имененм файла, потом точка и имя файла

            file.transferTo(new File(uploadPath + "/"+resultFilename));//перемещаем файл от юзера на нашу папку uploads и в конце добавляем имя файла
            message.setFilename(resultFilename);
        }


        messageRepo.save(message);
        Iterable<Message> allMessages = messageRepo.findAll();
        model.put("messages", allMessages);
        model.put("filter", "");
        return "main";
    }

    @PostMapping("remove")
    public String removeById(@RequestParam int remove, Map<String, Object> model) {
        Optional<Message> byId = messageRepo.findById(remove);
        if (!byId.isPresent()) {
            model.put("remove", remove);
            return "not_exist";
        } else {
            messageRepo.deleteById(remove);
            Iterable<Message> allMessages = messageRepo.findAll();
            model.put("messages", allMessages);
            return "redirect:/main";
        }
    }

}