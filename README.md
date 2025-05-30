![waving](https://capsule-render.vercel.app/api?type=waving&height=200&text=VibeEditor&fontAlign=80&fontAlignY=40&color=gradient&customColorList=0,2,2,2,2,3)
[![Typing SVG](https://readme-typing-svg.demolab.com?font=IBM+Plex+Sans&weight=600&size=30&duration=2000&pause=1000&repeat=false&width=435&lines=SSAFY+12thSeoul+Fianl+A503)](https://git.io/typing-svg)

## 🚀 Vide Editor
```
VS Code에서 코드/디렉토리/로그 스냅샷을 찍고, AI의 도움으로 기술 블로그 초안을 생성해 Notion에 바로 게시할 수 있는 확장 프로그램입니다.
```
<!-- [vibeEditor 사이트 바로 가기](https://vibeeditor.site/) -->

## 🔗 아키텍처 구성도 
<img src="https://i.imgur.com/JR51g4a.png" />

## 🔗 기술 스택
<img src="https://i.imgur.com/0o0k19G.png" />

## 🔗🫙 ERD
<img src="https://i.imgur.com/RIegDQH.png"/>


## 🧑‍💻Members
<div align="center">
<table>
  <thead>
  <tr>
    <th align="center"><a href="https://lab.ssafy.com/ehfql6363"><img src="https://secure.gravatar.com/avatar/2c04cc8c5af13bc0bbf7fde5fba124e8199c64ec411444d907ac1e86cf1d73b9?s=1600&d=identicon" width="100px;" /><sub></sub></a></th>
    <th align="center"><a href="https://lab.ssafy.com/jemilykoo"><img src="https://lab.ssafy.com/uploads/-/system/user/avatar/21703/avatar.png?width=800" width="100px;" /><sub></sub></a></th>
    <th align="center"><a href="https://lab.ssafy.com/uts417923"><img src="https://secure.gravatar.com/avatar/f368d9c785fb8db8a1f9c83a0477748a4e89042f6a4386dae98e9e2eb74bda11?s=1600&d=identicon" width="100px;" /><sub></sub></a></th>
    <th align="center"><a href="https://lab.ssafy.com/yunho_yun"><img src="https://secure.gravatar.com/avatar/afa4961de63b1187e0627da4487ddaeaa4c5af353b9dd06f6c09d1d408f568dc?s=1600&d=identicon" width="100px;" /><sub></sub></a></th>
    <th align="center"><a href="https://lab.ssafy.com/david8943"><img src="https://secure.gravatar.com/avatar/09320c98741077f0eff5215346ce04bd3b2a27515f4bf93bbd86e47dad551c94?s=1600&d=identicon" width="100px;" /><sub></sub></a></th>
    <th align="center"><a href="https://lab.ssafy.com/skydh507"><img src="https://lab.ssafy.com/uploads/-/system/user/avatar/22352/avatar.png?width=800" width="100px;" /><sub></sub></a></th>

  </tr>
  </thead>
  <tbody>
    <tr>
    <td align="center"><a href="https://lab.ssafy.com/ehfql6363">김동열</a>👑BE</td>
    <td align="center"><a href="https://lab.ssafy.com/jemilykoo">구정은</a>FE</td>
    <td align="center"><a href="https://lab.ssafy.com/uts417923">엄지우</a>FE</td>
    <td align="center"><a href="https://lab.ssafy.com/yunho_yun">윤윤호</a>BE,INFRA</td>
    <td align="center"><a href="https://lab.ssafy.com/david8943">송창근</a>FE</td>
    <td align="center"><a href="https://lab.ssafy.com/skydh507">이동현</a>BE,INFRA</td>
  </tr>
    </tbody>
</table>
</div>

---

## 🛠️ 설치 방법

- VS Code 확장 탭에서 `vibeeditor` 검색 후 설치
- 또는 `vscode://extension/VibeEditor.vibeditor` 링크를 통해 직접 설치

---

## 🧭 전체 사용 흐름 (Step by Step)

1. **로그인**

   - `Google` 또는 `GitHub` 계정으로 로그인합니다.

2. **Notion 데이터베이스 연결**

   - 설정 페이지에서 `노션 PRIVATE API 키 설정`을 통해 토큰을 입력합니다.
   - Notion에서 [API 통합 생성](https://www.notion.so/my-integrations) → 새 페이지 생성 → 빈 데이터베이스 추가 → API 통합 연결
   - 설정 페이지의 `노션 데이터베이스` 섹션에서 `+` 버튼 클릭 후 이름과 ID 입력

3. **템플릿 만들기**

   - `Ctrl + Shift + P`를 눌러 명령어 팔레트를 열고 `Vibe Editor: 새 템플릿 생성`을 실행합니다.
   - 템플릿 이름을 입력합니다.

4. **포스트 종류 선택**

   - 템플릿 생성 후, CS 개념 또는 트러블 슈팅 중 하나를 선택합니다.

5. **스냅샷 추가 (코드 / 디렉토리 / 로그)**

   - **코드 스냅샷**: 코드 에디터에서 원하는 영역을 드래그한 뒤 `코드 스냅샷 촬영`을 실행합니다. 코드, 경로, 라인 정보가 자동으로 저장됩니다.
   - **디렉토리 구조**: VS Code 탐색기에서 폴더를 우클릭하고 `디렉토리 구조 내보내기`를 선택하면 해당 구조가 스냅샷으로 저장됩니다.
   - **로그 스냅샷**: 터미널에서 로그를 드래그하거나 복사한 뒤 `복사한 로그 스냅샷 촬영하기`를 실행하면 시간과 함께 저장됩니다.

6. **설명 작성**

   - 템플릿에서 각 코드 스냅샷에 대해 설명을 작성합니다.

7. **프롬프트 추가**

   - AI에게 원하는 글 생성 방향을 안내할 수 있는 프롬프트를 입력합니다.

8. **포스트 생성 및 Notion 업로드**
   - 모든 입력을 마치면 `포스트 생성` 버튼을 누릅니다.
   - 생성된 포스트에서 `Notion에 게시` 버튼을 클릭하면 글이 자동으로 업로드됩니다.

---

## 🚀 주요 기능 요약

| 기능                           | 설명                                                      |
| ------------------------------ | --------------------------------------------------------- |
| ✅ 코드, 디렉토리, 로그 스냅샷 | 클릭 한 번으로 내용과 메타정보를 정리하여 사이드바에 저장 |
| ✍️ AI 글 생성                  | 스냅샷을 기반으로 기술 블로그 초안 자동 작성              |
| 🧠 말투 & 이모지 설정          | 친근한 말투부터 문어체까지 다양하게 선택 가능             |
| 🗂️ Notion 연동                 | 원하는 워크스페이스 및 DB에 클릭 한 번으로 게시           |

---

## 🔐 로그인

- Google 또는 GitHub 계정으로 로그인할 수 있습니다.
- 로그인 상태는 자동 유지되며, 로그아웃이 필요할 경우 설정 페이지 하단의 `로그아웃` 버튼을 클릭하면 됩니다.

---

## 📸 코드 스냅샷 찍기

1. 코드 에디터에서 원하는 코드를 드래그하세요.
2. 우클릭 후 `코드 스냅샷 촬영` 명령어를 실행하세요.
3. 선택한 코드, 파일 경로, 라인 번호가 함께 저장됩니다.
4. 사이드바의 `코드 스냅샷` 뷰에서 확인하고 클릭하면 코드만 깔끔하게 보여줍니다.

---

## 🌳 디렉토리 구조 내보내기

1. VS Code 탐색기에서 폴더를 우클릭합니다.
2. `디렉토리 구조 내보내기` 명령어를 실행합니다.
3. 해당 폴더 및 하위 폴더/파일 구조가 자동으로 정리되어 스냅샷으로 저장됩니다.
4. 사이드바 `디렉토리 구조 시각화` 뷰에서 확인할 수 있습니다.

---

## 🧾 로그 스냅샷 찍기

1. 터미널에서 원하는 로그를 드래그하거나 복사합니다.
2. 우클릭 후 `복사한 로그 스냅샷 촬영하기` 명령어를 실행합니다.
3. 선택한 로그와 함께 시간이 기록되어 저장됩니다.
4. 사이드바 `로그 스냅샷` 뷰에서 확인하세요.

---

## ✍️ 글 쓰기 (코드 기반 블로그 생성)

1. 스냅샷 뷰에서 원하는 코드, 로그, 디렉토리 중 하나를 선택합니다.
2. `코드 기반 글 작성` 명령어를 실행하면 AI가 자동으로 초안을 생성합니다.
3. 말투, 이모지 사용 여부, 템플릿 등을 설정할 수 있습니다.

---

## 🔗 Notion 연동

Vibe Editor를 통해 Notion에 글을 게시하려면 아래 순서를 따라 설정해주세요.

1. [Notion API 통합 생성](https://www.notion.so/my-integrations)

   - `+ 새 API 통합` 버튼을 클릭해 새로운 통합을 생성합니다.
   - 생성 후 발급된 **프라이빗 API 통합 시크릿**을 복사해둡니다.

2. Notion에서 빈 데이터베이스 생성

   - 새 페이지를 만든 후, 하단 `시작하기`에서 `데이터베이스` → `빈 데이터베이스`를 클릭합니다.
   - 오른쪽 상단 `...` 클릭 → `연결` → 1단계에서 만든 Integration을 선택해 연결합니다.

3. Vibe Editor에 API 키 등록

   - 설정 페이지에서 `노션 PRIVATE API 키 설정` 버튼 클릭하거나,
   - 명령어 팔레트(`Ctrl + Shift + P`)에서 `노션 PRIVATE API 키 설정`을 실행해 토큰을 붙여넣습니다.

4. Notion 데이터베이스 등록

   - 설정 페이지의 `노션 데이터베이스` 섹션에서 `+` 버튼 클릭
   - 이름은 자유롭게 작성하고, **데이터베이스 ID**를 입력합니다.  
     예:
     ```
     https://www.notion.so/1f17c33f8837809eb99bea3t2e4efb29?v=abcd123...
     ```
     → `?v=` 앞의 `1f17c33f8837809eb99bea3t2e4efb29` 이 부분이 **데이터베이스 ID**입니다.

5. 게시하기
   - 설정이 완료되면, 원하는 포스트를 선택한 뒤 `Notion에 게시` 버튼을 눌러 글을 바로 업로드할 수 있습니다.

---

## 📞 문의 및 기여

- 문제가 발생하거나 기능 제안이 있다면 [이슈 페이지](https://github.com/your-org/vibe-editor/issues)를 이용해주세요.
- Pull Request도 환영합니다! 😊

---

## 🧠 License

MIT License
