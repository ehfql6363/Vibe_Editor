package com.ssafy.vibe.notion.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class NotionPageRequestFactory {
	public Map<String, Object> createPageRequest(String databaseId, String title, List<Map<String, Object>> children) {
		Map<String, Object> request = new HashMap<>();

		Map<String, Object> parent = new HashMap<>();
		parent.put("type", "database_id");
		parent.put("database_id", databaseId);
		request.put("parent", parent);

		Map<String, Object> titleProperty = new HashMap<>();
		titleProperty.put("title", createRichTextArray(title));

		Map<String, Object> properties = new HashMap<>();
		properties.put("title", titleProperty);
		request.put("properties", properties);

		request.put("children", children);

		return request;
	}

	private List<Map<String, Object>> createRichTextArray(String text) {
		Map<String, Object> richText = new HashMap<>();
		Map<String, Object> textContent = new HashMap<>();
		textContent.put("content", text);
		richText.put("type", "text");
		richText.put("text", textContent);
		return List.of(richText);
	}
}
