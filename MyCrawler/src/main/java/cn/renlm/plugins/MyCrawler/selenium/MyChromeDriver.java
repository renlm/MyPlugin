package cn.renlm.plugins.MyCrawler.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * 驱动包装
 * 
 * @author Renlm
 *
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class MyChromeDriver {

	@NonNull
	private WebDriver webDriver;

	@NonNull
	private ChromeDriverService service;

}