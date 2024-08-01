package org.example.app.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class PropertiesConfig {

	@Value("${javafx.main.tree}")
	private Boolean javafxMainTree;

	@Value("${javafx.main.toolbar}")
	private Boolean javafxMainToolbar;
}
