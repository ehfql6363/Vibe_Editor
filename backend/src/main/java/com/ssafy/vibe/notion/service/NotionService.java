package com.ssafy.vibe.notion.service;

import java.util.List;

import com.ssafy.vibe.notion.service.command.NotionConnectInfoCommand;
import com.ssafy.vibe.notion.service.command.NotionRegisterDatabaseCommand;
import com.ssafy.vibe.notion.service.command.RetrieveNotionDatabasesCommand;
import com.ssafy.vibe.notion.service.dto.RetrieveNotionDatabasesDTO;

public interface NotionService {

	void saveNotionKey(NotionConnectInfoCommand command);

	void registerNotionDatabase(NotionRegisterDatabaseCommand command);

	List<RetrieveNotionDatabasesDTO> retrieveNotionDatabases(RetrieveNotionDatabasesCommand command);
}
