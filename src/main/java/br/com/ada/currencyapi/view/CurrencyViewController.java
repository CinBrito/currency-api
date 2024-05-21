package br.com.ada.currencyapi.view;

import br.com.ada.currencyapi.domain.CurrencyRequest;
import br.com.ada.currencyapi.exception.CurrencyException;
import br.com.ada.currencyapi.service.CurrencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/currencyview")
public class CurrencyViewController {

    private final CurrencyService currencyService;

    @GetMapping
    public String get() {
        return "list-currencies";
    }

    @GetMapping("/new-currency")
    public String showCreateForm(Model model) {
        model.addAttribute("currencyRequest", new CurrencyRequest());
        return "new-currency";
    }

    @PostMapping("/new-currency")
    public String createCurrency(@Valid @ModelAttribute("currencyRequest") CurrencyRequest currencyRequest,
                                 Model model) {

        try {
            Long id = currencyService.create(currencyRequest);
        } catch (CurrencyException e) {
            model.addAttribute("error", e.getMessage());
            return "createCurrency";
        }

        return "redirect:/list-currencies"; // Redirect to a list of currencies or a success page
    }
}

