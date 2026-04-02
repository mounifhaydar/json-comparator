package com.comparator.model.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "comparator.prop")
public class ComparatorAppProperties {
	private String	envEncKey;
	private String	envEncIv;
	private String	envConfigPath;

	private int		webClientMemorySize;				//16777216
	private boolean	disableActionsPermissionsSecurity;	//TRUE
	private boolean	enableDocumentsEncryption;			//@Value("${documents.enable.encryption}") Boolean enableDocumentsEncryption = false
}
