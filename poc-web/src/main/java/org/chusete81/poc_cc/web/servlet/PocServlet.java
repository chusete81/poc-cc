package org.chusete81.poc_cc.web.servlet;

import org.chusete81.poc_cc.web.client.HubClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.chusete81.poc_cc.common.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;

@Controller
@SessionAttributes({"uuid", "nif", "timestamp"})
public class PocServlet {

    private static final int GLOBAL_USER_TIMEOUT = Config.getGlobalUserTimeoutMS();

    private static final Logger log = LogManager.getLogger(PocServlet.class);

    @Autowired
    private HubClient hubClient;

    @GetMapping("/")
    public String index () {
        return "form";
    }

    @PostMapping("/newRequest")
    public RedirectView newRequest (@RequestParam(name="nif", required=true, defaultValue="") String nif, Model model) {
        log.info(String.format("[PocServlet.newRequest(%s)]", nif));

        if (nif.isEmpty())
            return new RedirectView("/");

        log.debug("Invocando a Hub");

        String uuid = hubClient.newRequest(nif);

        log.debug(String.format("NIF: %s  UUID: %s", nif, uuid));

        model.addAttribute("uuid", uuid);
        model.addAttribute("nif", nif);
        model.addAttribute("timestamp", new Date().getTime());

        return new RedirectView("/status");
    }

    @GetMapping("/status")
    public String getStatus (Model model) {
        String uuid = (String) model.asMap().get("uuid");
        log.info(String.format("[PocServlet.getStatus(%s)]", uuid));

        String reqStatus = hubClient.getStatus(uuid);
        model.addAttribute("reqStatus", formatResponse(reqStatus));

        Long timestamp = (Long) model.asMap().get("timestamp");
        Long elapsedTime = new Date().getTime() - timestamp;

        if (elapsedTime > GLOBAL_USER_TIMEOUT)
            return "final";

        return "partial";
    }

    @GetMapping("/shutdown")
    public void shutdown () {
        // TODO cascade shutdown
        System.exit(0);
    }

    private String formatResponse (String msg) {
        return msg.replace("\n", "<br/>");
    }
}
