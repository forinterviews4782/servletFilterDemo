package com.demo.controlers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoControler {
	@GetMapping("/demo")
	public String demoMethod(@RequestParam int milliseconds, @RequestParam int stringchars) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			return "Returning sooner then expected.";
		}
		String returnValue = "";
		for (int i = 0; i < stringchars; i++) {
			returnValue += "a";
		}
		return returnValue;
	}
}
