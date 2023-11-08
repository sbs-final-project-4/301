package com.yk.Motivation.domain.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yk.Motivation.base.rq.Rq;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.member.service.MemberService;
import com.yk.Motivation.domain.order.entity.Order;
import com.yk.Motivation.domain.order.exception.MemberCanNotSeeOrderException;
import com.yk.Motivation.domain.order.exception.OrderIdNotMatchedException;
import com.yk.Motivation.domain.order.service.OrderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/usr/order")
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final OrderService orderService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showDetail(
            @PathVariable long id,
            Model model
    ) {
        Order order = orderService.findForPrintById(id).get();

        Member member = rq.getMember();

        if (orderService.memberCanSee(member, order) == false) {
            throw new MemberCanNotSeeOrderException();
        }

        model.addAttribute("order", order);

        return "usr/order/detail";
    }

    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }

    private final String SECRET_KEY = "시크릿 키";

    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model
    ) throws Exception {

        Order order = orderService.findForPrintById(id).get();

        long orderIdInputed = Long.parseLong(orderId.split("__")[1]);

        if (id != orderIdInputed) {
            throw new OrderIdNotMatchedException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        Member member = rq.getMember();

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            orderService.payByTossPayments(order, paymentKey);

            return rq.redirectOrBack("/usr/order/%d".formatted(order.getId()), RsData.of("S-1","결제가 완료되었습니다."));
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "usr/order/fail";
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "usr/order/fail";
    }

    @PostMapping("/makeOrder")
    @PreAuthorize("isAuthenticated()")
    public String makeOrder() {
        Member member = rq.getMember();
        Order order = orderService.createFromCart(member);

        return rq.redirectOrBack("/usr/order/%d".formatted(order.getId()), RsData.of("S-1", "%d번 주문이 생성되었습니다.".formatted(order.getId())));
    }

    @RequestMapping("/refund/{id}")
    @ResponseBody
    public RsData refund(
            @PathVariable Long id
    ) throws Exception {

        Order order = orderService.findForPrintById(id).get();
        String paymentKey = order.getPaymentKey();
        String reason = "단순 변심";

        // 요청 URL
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        // HTTP 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.add("Content-Type", "application/json");

        // 요청 본문 데이터 생성
        Map<String, String> body = new HashMap<>();
        body.put("cancelReason", "단순 변심");

        // HttpEntity 객체 생성 (헤더 + 본문)
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        // RestTemplate 생성 및 요청 보내기
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // 상태 코드, 헤더, 본문 출력
            System.out.println("Status Code: " + responseEntity.getStatusCode());
            System.out.println("Headers: " + responseEntity.getHeaders());
            System.out.println("Response Body: " + responseEntity.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return RsData.of("S-1", "성공했으면 좋겠다...", null);
    }
}