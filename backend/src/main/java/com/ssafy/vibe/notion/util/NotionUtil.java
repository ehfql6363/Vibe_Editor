package com.ssafy.vibe.notion.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.Parser;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Markdown을 Notion 스타일 블록 형태로 변환하는 유틸리티 클래스.
 */
@Slf4j
@Component
public class NotionUtil extends AbstractVisitor {

	private final Parser parser = Parser.builder().build();

	public List<Map<String, Object>> parseMarkdownToNotionBlocks(String markdown) {
		// 마크다운 테이블 처리 (파서가 처리하기 전에)
		List<Map<String, Object>> blocks = new ArrayList<>();

		// 마크다운 테이블 패턴 확인
		Pattern tablePattern = Pattern.compile("\\|(.+?)\\|\\s*\\n\\|\\s*[-:]+[-\\s|:]*\\|\\s*\\n((\\|.+?\\|\\s*\\n)+)",
			Pattern.MULTILINE);
		Matcher tableMatcher = tablePattern.matcher(markdown);

		// 마크다운에서 테이블 추출하여 별도 처리
		StringBuilder newMarkdown = new StringBuilder();
		Map<String, Object> tableBlock = new HashMap<>();
		int lastEnd = 0;

		while (tableMatcher.find()) {
			// 테이블 시작 전까지의 텍스트 추가
			newMarkdown.append(markdown, lastEnd, tableMatcher.start());
			lastEnd = tableMatcher.end();

			// 테이블 처리하고 블록에 추가
			String tableText = tableMatcher.group(0);
			tableBlock = processMarkdownTable(tableText);

			// 마크다운에 플레이스홀더 추가 (나중에 파싱되지 않도록)
			newMarkdown.append("<!-- TABLE_PLACEHOLDER -->\n");
		}

		// 남은 텍스트 추가
		newMarkdown.append(markdown.substring(lastEnd));

		// 수정된 마크다운으로 파싱 계속
		String processedMarkdown = newMarkdown.toString();
		Node document = parser.parse(processedMarkdown);

		// Process the document
		Map<String, Object> finalTableBlock = tableBlock;
		document.accept(new AbstractVisitor() {
			@Override
			public void visit(Heading heading) {
				blocks.add(createHeadingBlock(heading));
				visitChildren(heading);
			}

			@Override
			public void visit(Paragraph paragraph) {

				if (containsOnlyImage(paragraph)) {
					Image image = findFirstImage(paragraph);
					blocks.add(createImageBlock(Objects.requireNonNull(image)));
				} else {
					// 플레이스홀더가 아닌 경우만 추가 (테이블 플레이스홀더 건너뛰기)
					String content = getTextContent(paragraph);
					log.info("content: {}", content);
					if (!content.trim().equals("<!-- TABLE_PLACEHOLDER -->")) {
						blocks.add(createParagraphBlock(paragraph));
						List<Map<String, Object>> richText = createFormattedRichText(content);
						blocks.getLast().put("paragraph", Map.of("rich_text", richText)); // 리팩토링 필요
					}
				}
				visitChildren(paragraph);
			}

			@Override
			public void visit(BulletList bulletList) {
				processBulletList(bulletList, blocks);
			}

			@Override
			public void visit(OrderedList orderedList) {
				processOrderedList(orderedList, blocks);
			}

			@Override
			public void visit(Code code) {
				blocks.add(createCodeBlock(code));
				visitChildren(code);
			}

			@Override
			public void visit(FencedCodeBlock codeBlock) {
				blocks.add(createFencedCodeBlock(codeBlock));
				visitChildren(codeBlock);
			}

			@Override
			public void visit(BlockQuote blockQuote) {
				blocks.add(createQuoteBlock(blockQuote));
				visitChildren(blockQuote);
			}

			@Override
			public void visit(ThematicBreak thematicBreak) {
				blocks.add(createDividerBlock());
				visitChildren(thematicBreak);
			}

			@Override
			public void visit(Link link) {
				// Links are handled within paragraph and other block processing
				visitChildren(link);
			}

			@Override
			public void visit(HtmlBlock htmlBlock) {
				// Process HTML tables
				log.info("html block: {}", htmlBlock.getLiteral());
				if (htmlBlock.getLiteral().contains("<table")) {
					blocks.add(processHtmlTable(htmlBlock.getLiteral()));
				} else if (htmlBlock.getLiteral().equals("<!-- TABLE_PLACEHOLDER -->")) {
					blocks.add(finalTableBlock);
				} else {
					// For other HTML, convert to paragraph for now
					Map<String, Object> paragraphBlock = new HashMap<>();
					paragraphBlock.put("type", "paragraph");

					Map<String, Object> paragraph = new HashMap<>();
					paragraph.put("rich_text", List.of(createRichText(htmlBlock.getLiteral())));
					paragraphBlock.put("paragraph", paragraph);

					blocks.add(paragraphBlock);
				}
				visitChildren(htmlBlock);
			}
		});

		return blocks;
	}

	private Map<String, Object> createHeadingBlock(Heading heading) {
		Map<String, Object> headingBlock = new HashMap<>();
		String headingType = "heading_" + heading.getLevel();
		headingBlock.put("type", headingType);

		Map<String, Object> headingContent = new HashMap<>();
		headingContent.put("rich_text", extractRichTextList(heading));
		headingContent.put("color", "default");

		headingBlock.put(headingType, headingContent);
		return headingBlock;
	}

	private Map<String, Object> createParagraphBlock(Paragraph paragraph) {
		Map<String, Object> paragraphBlock = new HashMap<>();
		paragraphBlock.put("type", "paragraph");

		Map<String, Object> paragraphContent = new HashMap<>();
		paragraphContent.put("rich_text", extractRichTextList(paragraph));
		paragraphContent.put("color", "default");

		paragraphBlock.put("paragraph", paragraphContent);
		return paragraphBlock;
	}

	private Map<String, Object> createCodeBlock(Code code) {
		Map<String, Object> codeBlock = new HashMap<>();
		codeBlock.put("type", "code");

		Map<String, Object> codeContent = new HashMap<>();
		codeContent.put("rich_text", List.of(createRichText(code.getLiteral())));
		codeContent.put("language", "plain text");

		codeBlock.put("code", codeContent);
		return codeBlock;
	}

	private Map<String, Object> createFencedCodeBlock(FencedCodeBlock codeBlock) {
		Map<String, Object> block = new HashMap<>();
		block.put("type", "code");

		Map<String, Object> codeContent = new HashMap<>();

		// Convert null or empty language to plain text
		String language = codeBlock.getInfo();
		if (language == null || language.isEmpty()) {
			language = "plain text";
		} else if (language.equals("python")) {
			language = "python";
		} else if (language.equals("java")) {
			language = "java";
		} else if (language.equals("javascript")) {
			language = "javascript";
		} else {
			language = "plain text"; // Default fallback
		}

		codeContent.put("language", language);
		codeContent.put("rich_text", List.of(createRichText(codeBlock.getLiteral())));

		block.put("code", codeContent);
		return block;
	}

	private Map<String, Object> createQuoteBlock(BlockQuote blockQuote) {
		Map<String, Object> quoteBlock = new HashMap<>();
		quoteBlock.put("type", "quote");

		// Extract text from the blockquote
		StringBuilder quoteText = new StringBuilder();
		blockQuote.getFirstChild().accept(new AbstractVisitor() {
			@Override
			public void visit(Text text) {
				quoteText.append(text.getLiteral());
				visitChildren(text);
			}
		});

		Map<String, Object> quoteContent = new HashMap<>();
		quoteContent.put("rich_text", List.of(createRichText(quoteText.toString())));
		quoteContent.put("color", "default");

		quoteBlock.put("quote", quoteContent);
		return quoteBlock;
	}

	private Map<String, Object> createDividerBlock() {
		Map<String, Object> dividerBlock = new HashMap<>();
		dividerBlock.put("type", "divider");
		dividerBlock.put("divider", new HashMap<>());
		return dividerBlock;
	}

	private Map<String, Object> createImageBlock(Image image) {
		Map<String, Object> imageBlock = new HashMap<>();
		imageBlock.put("type", "image");

		Map<String, Object> imageContent = new HashMap<>();
		imageContent.put("type", "external");
		imageContent.put("external", Map.of("url", image.getDestination()));

		if (image.getTitle() != null && !image.getTitle().isEmpty()) {
			imageContent.put("caption", List.of(createRichText(image.getTitle())));
		}

		imageBlock.put("image", imageContent);
		return imageBlock;
	}

	private void processBulletList(BulletList bulletList, List<Map<String, Object>> blocks) {
		Node listItem = bulletList.getFirstChild();
		while (listItem != null) {
			Map<String, Object> bulletItemBlock = new HashMap<>();
			bulletItemBlock.put("type", "bulleted_list_item");

			Map<String, Object> bulletItemContent = new HashMap<>();
			List<Map<String, Object>> richTextList = new ArrayList<>();

			// Process list item text
			Node paragraph = listItem.getFirstChild();
			if (paragraph instanceof Paragraph) {
				richTextList.addAll(extractRichTextList(paragraph));
			}

			bulletItemContent.put("rich_text", richTextList);
			bulletItemContent.put("color", "default");

			// Check for nested lists
			Node secondChild = paragraph.getNext();
			if (secondChild instanceof BulletList || secondChild instanceof OrderedList) {
				List<Map<String, Object>> children = new ArrayList<>();
				if (secondChild instanceof BulletList) {
					processBulletListAsChildren((BulletList)secondChild, children);
				} else {
					processOrderedListAsChildren((OrderedList)secondChild, children);
				}
				bulletItemContent.put("children", children);
			}

			bulletItemBlock.put("bulleted_list_item", bulletItemContent);
			blocks.add(bulletItemBlock);

			listItem = listItem.getNext();
		}
	}

	private void processBulletListAsChildren(BulletList bulletList, List<Map<String, Object>> children) {
		Node listItem = bulletList.getFirstChild();
		while (listItem != null) {
			Map<String, Object> bulletItemBlock = new HashMap<>();
			bulletItemBlock.put("type", "bulleted_list_item");

			Map<String, Object> bulletItemContent = new HashMap<>();
			List<Map<String, Object>> richTextList = new ArrayList<>();

			// Process list item text
			Node paragraph = listItem.getFirstChild();
			if (paragraph instanceof Paragraph) {
				richTextList.addAll(extractRichTextList(paragraph));
			}

			bulletItemContent.put("rich_text", richTextList);
			bulletItemContent.put("color", "default");

			bulletItemBlock.put("bulleted_list_item", bulletItemContent);
			children.add(bulletItemBlock);

			listItem = listItem.getNext();
		}
	}

	private void processOrderedList(OrderedList orderedList, List<Map<String, Object>> blocks) {
		Node listItem = orderedList.getFirstChild();
		while (listItem != null) {
			Map<String, Object> numberedItemBlock = new HashMap<>();
			numberedItemBlock.put("type", "numbered_list_item");

			Map<String, Object> numberedItemContent = new HashMap<>();
			List<Map<String, Object>> richTextList = new ArrayList<>();

			// Process list item text
			Node paragraph = listItem.getFirstChild();
			if (paragraph instanceof Paragraph) {
				richTextList.addAll(extractRichTextList(paragraph));
			}

			numberedItemContent.put("rich_text", richTextList);
			numberedItemContent.put("color", "default");

			// Check for nested lists
			Node secondChild = paragraph.getNext();
			if (secondChild instanceof BulletList || secondChild instanceof OrderedList) {
				List<Map<String, Object>> children = new ArrayList<>();
				if (secondChild instanceof BulletList) {
					processBulletListAsChildren((BulletList)secondChild, children);
				} else {
					processOrderedListAsChildren((OrderedList)secondChild, children);
				}
				numberedItemContent.put("children", children);
			}

			numberedItemBlock.put("numbered_list_item", numberedItemContent);
			blocks.add(numberedItemBlock);

			listItem = listItem.getNext();
		}
	}

	private void processOrderedListAsChildren(OrderedList orderedList, List<Map<String, Object>> children) {
		Node listItem = orderedList.getFirstChild();
		while (listItem != null) {
			Map<String, Object> numberedItemBlock = new HashMap<>();
			numberedItemBlock.put("type", "numbered_list_item");

			Map<String, Object> numberedItemContent = new HashMap<>();
			List<Map<String, Object>> richTextList = new ArrayList<>();

			// Process list item text
			Node paragraph = listItem.getFirstChild();
			if (paragraph instanceof Paragraph) {
				richTextList.addAll(extractRichTextList(paragraph));
			}

			numberedItemContent.put("rich_text", richTextList);
			numberedItemContent.put("color", "default");

			numberedItemBlock.put("numbered_list_item", numberedItemContent);
			children.add(numberedItemBlock);

			listItem = listItem.getNext();
		}
	}

	private Map<String, Object> processHtmlTable(String htmlTable) {
		// 개선된 HTML 테이블 처리
		Map<String, Object> tableBlock = new HashMap<>();
		tableBlock.put("type", "table");

		Map<String, Object> tableContent = new HashMap<>();

		// 더 견고한 정규식 패턴으로 수정
		Pattern rowPattern = Pattern.compile("<tr[^>]*>(.*?)</tr>", Pattern.DOTALL);
		Matcher rowMatcher = rowPattern.matcher(htmlTable);

		List<List<List<Map<String, Object>>>> rows = new ArrayList<>();
		boolean hasHeader = false;
		int columnCount = 0;

		while (rowMatcher.find()) {
			String rowHtml = rowMatcher.group(1);
			// th와 td를 모두 포착하도록 패턴 개선
			Pattern cellPattern = Pattern.compile("<(td|th)[^>]*>(.*?)</\\1>", Pattern.DOTALL);
			Matcher cellMatcher = cellPattern.matcher(rowHtml);

			List<List<Map<String, Object>>> cells = new ArrayList<>();
			while (cellMatcher.find()) {
				String cellType = cellMatcher.group(1); // th인지 td인지 확인
				String cellContent = cellMatcher.group(2).trim();

				// HTML 태그 제거 (기본적인 방식)
				cellContent = cellContent.replaceAll("<[^>]*>", "");

				// 마크다운 강조 구문 처리
				cellContent = processCellMarkdown(cellContent);

				List<Map<String, Object>> richText = createFormattedRichText(cellContent);
				cells.add(richText);

				// 헤더 행 여부 확인
				if (cellType.equals("th")) {
					hasHeader = true;
				}
			}

			if (cells.size() > columnCount) {
				columnCount = cells.size();
			}

			rows.add(cells);
		}

		tableContent.put("table_width", columnCount);
		tableContent.put("has_column_header", hasHeader);
		tableContent.put("has_row_header", false);
		tableContent.put("children", createTableRows(rows));

		tableBlock.put("table", tableContent);
		return tableBlock;
	}

	// 마크다운 테이블 처리 메서드 추가
	private Map<String, Object> processMarkdownTable(String markdownTable) {
		Map<String, Object> tableBlock = new HashMap<>();
		tableBlock.put("type", "table");

		Map<String, Object> tableContent = new HashMap<>();

		// 테이블 행 분리
		String[] lines = markdownTable.split("\n");

		// 헤더 행 추출
		String headerLine = lines[0].trim();

		// 구분자 행은 건너뛰기

		// 열 개수 계산
		String[] headerCells = extractRowCells(headerLine);
		int columnCount = headerCells.length;

		// 테이블 데이터 생성
		List<List<List<Map<String, Object>>>> rows = new ArrayList<>();

		// 헤더 행 처리
		List<List<Map<String, Object>>> headerCellsData = new ArrayList<>();
		for (String cell : headerCells) {
			headerCellsData.add(createFormattedRichText(cell));
		}
		rows.add(headerCellsData);

		// 데이터 행 처리 (구분자 행 건너뛰기)
		for (int i = 2; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.isEmpty())
				continue;

			String[] cells = extractRowCells(line);
			List<List<Map<String, Object>>> rowData = new ArrayList<>();

			for (int j = 0; j < columnCount; j++) {
				String cellContent = j < cells.length ? cells[j] : "";
				rowData.add(createFormattedRichText(cellContent));
			}

			rows.add(rowData);
		}

		tableContent.put("table_width", columnCount);
		tableContent.put("has_column_header", true); // 마크다운 테이블은 첫 행이 헤더로 간주
		tableContent.put("has_row_header", false);
		tableContent.put("children", createTableRows(rows));

		tableBlock.put("table", tableContent);
		return tableBlock;
	}

	// 마크다운 테이블 행에서 셀 추출
	private String[] extractRowCells(String row) {
		// 양끝 파이프 제거 후 셀 분리
		String trimmedRow = row.trim();
		if (trimmedRow.startsWith("|")) {
			trimmedRow = trimmedRow.substring(1);
		}
		if (trimmedRow.endsWith("|")) {
			trimmedRow = trimmedRow.substring(0, trimmedRow.length() - 1);
		}

		// 셀 분리 및 트림
		String[] cells = trimmedRow.split("\\|");
		for (int i = 0; i < cells.length; i++) {
			cells[i] = cells[i].trim();
		}

		return cells;
	}

	// 서식이 있는 텍스트 생성 (마크다운 서식 처리)
	private List<Map<String, Object>> createFormattedRichText(String content) {
		List<Map<String, Object>> richTextList = new ArrayList<>();

		// 강조 구문 처리
		processTextFormatting(content, richTextList);

		return richTextList.isEmpty()
			? List.of(createRichText(""))
			: richTextList;
	}

	// 텍스트 내 강조 구문 처리
	private void processTextFormatting(String content, List<Map<String, Object>> richTextList) {
		// 볼드 처리 (**텍스트**)
		Pattern boldPattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
		Pattern italicPattern = Pattern.compile("\\*(.*?)\\*");
		Matcher boldMatcher = boldPattern.matcher(content);

		if (boldMatcher.find()) {
			StringBuilder processed = new StringBuilder();
			int lastEnd = 0;

			do {
				// 강조 전 텍스트 추가
				if (boldMatcher.start() > lastEnd) {
					String beforeText = content.substring(lastEnd, boldMatcher.start());
					if (!beforeText.isEmpty()) {
						richTextList.add(createRichText(beforeText));
					}
				}

				// 강조 텍스트 추가
				String boldText = boldMatcher.group(1);
				Map<String, Object> richText = createRichText(boldText);
				Map<String, Object> annotations = (Map<String, Object>)richText.get("annotations");
				annotations.put("bold", true);
				richTextList.add(richText);

				lastEnd = boldMatcher.end();
			} while (boldMatcher.find());

			// 나머지 텍스트 추가
			if (lastEnd < content.length()) {
				String remainingText = content.substring(lastEnd);
				if (!remainingText.isEmpty()) {
					richTextList.add(createRichText(remainingText));
				}
			}
		} else {
			// 강조 없으면 그냥 텍스트로 추가
			richTextList.add(createRichText(content));
		}
	}

	// 셀 내용의 마크다운 처리 (강조 등)
	private String processCellMarkdown(String cellContent) {
		// 이 메서드에서는 마크다운 구문을 파싱하지 않고 그대로 반환
		// 실제 처리는 createFormattedRichText에서 수행
		return cellContent;
	}

	private List<Map<String, Object>> createTableRows(List<List<List<Map<String, Object>>>> rows) {
		List<Map<String, Object>> tableRows = new ArrayList<>();

		for (List<List<Map<String, Object>>> row : rows) {
			Map<String, Object> tableRow = new HashMap<>();
			tableRow.put("type", "table_row");
			tableRow.put("table_row", Map.of("cells", row));
			tableRows.add(tableRow);
		}

		return tableRows;
	}

	// 수정된 리치 텍스트 추출 메서드
	private List<Map<String, Object>> extractRichTextList(Node node) {
		List<Map<String, Object>> richTextList = new ArrayList<>();

		// 새로운 방문자를 생성하여 텍스트 형식을 처리
		TextFormattingVisitor visitor = new TextFormattingVisitor(richTextList);
		node.accept(visitor);

		return richTextList.isEmpty()
			? List.of(createRichText(""))
			: richTextList;
	}

	private String getTextContent(Node node) {
		StringBuilder text = new StringBuilder();
		node.accept(new AbstractVisitor() {
			@Override
			public void visit(Text text1) {
				text.append(text1.getLiteral());
				visitChildren(text1);
			}

			@Override
			public void visit(StrongEmphasis emphasis) {
				// 강조 구문 포함하여 추출
				StringBuilder emphasisText = new StringBuilder();
				emphasis.accept(new AbstractVisitor() {
					@Override
					public void visit(Text text1) {
						emphasisText.append(text1.getLiteral());
						visitChildren(text1);
					}
				});
				text.append(emphasisText);
			}

			@Override
			public void visit(Emphasis emphasis) {
				// 기울임 구문 포함하여 추출
				StringBuilder emphasisText = new StringBuilder();
				emphasis.accept(new AbstractVisitor() {
					@Override
					public void visit(Text text1) {
						emphasisText.append(text1.getLiteral());
						visitChildren(text1);
					}
				});
				text.append(emphasisText);
			}
		});
		return text.toString();
	}

	private Map<String, Object> createRichText(String content) {
		Map<String, Object> richText = new HashMap<>();
		richText.put("type", "text");

		Map<String, Object> textContent = new HashMap<>();
		textContent.put("content", content);
		textContent.put("link", null);
		richText.put("text", textContent);
		richText.put("plain_text", content);
		richText.put("href", null);

		Map<String, Object> annotations = new HashMap<>();
		annotations.put("bold", false);
		annotations.put("italic", false);
		annotations.put("strikethrough", false);
		annotations.put("underline", false);
		annotations.put("code", false);
		annotations.put("color", "default");
		richText.put("annotations", annotations);

		return richText;
	}

	private boolean containsOnlyImage(Paragraph paragraph) {
		if (paragraph.getFirstChild() instanceof Image && paragraph.getFirstChild() == paragraph.getLastChild()) {
			return true;
		}
		return false;
	}

	private Image findFirstImage(Node node) {
		if (node instanceof Image) {
			return (Image)node;
		}

		Node child = node.getFirstChild();
		while (child != null) {
			Image found = findFirstImage(child);
			if (found != null) {
				return found;
			}
			child = child.getNext();
		}

		return null;
	}

	// 텍스트 형식을 처리하기 위한 별도의 방문자 클래스
	private class TextFormattingVisitor extends AbstractVisitor {
		private final List<Map<String, Object>> richTextList;

		public TextFormattingVisitor(List<Map<String, Object>> richTextList) {
			this.richTextList = richTextList;
		}

		@Override
		public void visit(Text text) {
			richTextList.add(createRichText(text.getLiteral()));
			visitChildren(text);
		}

		@Override
		public void visit(Emphasis emphasis) {
			String content = getTextContent(emphasis);
			Map<String, Object> richText = createRichText(content);
			Map<String, Object> annotations = (Map<String, Object>)richText.get("annotations");
			annotations.put("italic", true);
			richTextList.add(richText); // 중요: 리스트에 추가
			// 자식 노드는 따로 방문하지 않음 (이미 텍스트를 추출했으므로)
		}

		@Override
		public void visit(StrongEmphasis strongEmphasis) {
			String content = getTextContent(strongEmphasis);
			Map<String, Object> richText = createRichText(content);
			Map<String, Object> annotations = (Map<String, Object>)richText.get("annotations");
			annotations.put("bold", true);
			richTextList.add(richText); // 중요: 리스트에 추가
			// 자식 노드는 따로 방문하지 않음
		}

		@Override
		public void visit(Link link) {
			String content = getTextContent(link);
			Map<String, Object> richText = createRichText(content);
			richText.put("href", link.getDestination());
			richTextList.add(richText); // 중요: 리스트에 추가
			// 자식 노드는 따로 방문하지 않음
		}

		@Override
		public void visit(Code code) {
			Map<String, Object> richText = createRichText(code.getLiteral());
			Map<String, Object> annotations = (Map<String, Object>)richText.get("annotations");
			annotations.put("code", true);
			richTextList.add(richText);
			// 자식 노드는 따로 방문하지 않음
		}
	}
}