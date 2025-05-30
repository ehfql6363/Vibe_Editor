-- User: 3개
-- Template: 8개
-- Snapshot: 15개
-- Prompt: 9개
-- Prompt: 9개
-- PromptAttach: 15개

-- 1. Users
INSERT INTO `users` (`user_id`, `user_name`, `email`, `provider_name`, `provider_uid`,
                     `notion_secret_key`, `notion_active`,
                     `is_deleted`, `last_login_at`, `created_at`, `updated_at`)
VALUES (1, '유저1', 'user1@example.com', 'github', 'uid-1', NULL, FALSE, FALSE, NULL, NOW(), NOW()),
       (2, '유저2', 'user2@example.com', 'google', 'uid-2', NULL, FALSE, FALSE, NULL, NOW(), NOW()),
       (3, '유저3', 'user3@example.com', 'github', 'uid-3', NULL, FALSE, FALSE, NULL, NOW(), NOW());

-- 2. Notion Database
INSERT INTO `notion_database` (`notion_id`, `user_id`, `database_name`, `database_uid`,
                               `is_deleted`, `created_at`, `updated_at`)
VALUES (1, 1, 'CS 스터디', 'notion-uid-1', FALSE, NOW(), NOW()),
       (2, 2, '면접 스터디', 'notion-uid-2', FALSE, NOW(), NOW()),
       (3, 3, '알고리즘 스터디', 'notion-uid-3', FALSE, NOW(), NOW());

-- 2. Templates
INSERT INTO `template` (`template_id`, `user_id`, `template_name`, `is_deleted`, `created_at`, `updated_at`)
VALUES
    -- 유저1 (user_id = 1) 템플릿 2개
    (1, 1, 'Spring Boot API 서버 구축', FALSE, NOW(), NOW()),
    (2, 1, 'Docker를 이용한 환경 분리', FALSE, NOW(), NOW()),

    -- 유저2 (user_id = 2) 템플릿 3개
    (3, 2, 'Redis를 활용한 캐시 시스템', FALSE, NOW(), NOW()),
    (4, 2, 'OAuth2 인증 시스템 구축', TRUE, NOW(), NOW()),
    (5, 2, 'Kafka 기반 이벤트 처리', FALSE, NOW(), NOW()),

    -- 유저3 (user_id = 3) 템플릿 3개
    (6, 3, 'React Query 상태 관리', FALSE, NOW(), NOW()),
    (7, 3, 'ElasticSearch 색인 최적화', FALSE, NOW(), NOW()),
    (8, 3, 'Kubernetes 배포 자동화', TRUE, NOW(), NOW());

-- 3. Snapshots
INSERT INTO `snapshot` (`snapshot_id`, `template_id`, `user_id`, `snapshot_name`, `snapshot_type`, `content`,
                        `is_deleted`, `created_at`, `updated_at`)
VALUES
    -- 유저1 템플릿1 (template_id = 1) BLOCK 3개(활성 2개, 비활성 1개) (총 3개)
    (1, 1, 1, 'Application.java', 'BLOCK', 'public class Application {...}', FALSE, NOW(), NOW()),
    (2, 1, 1, 'Controller.java', 'BLOCK', '@RestController public class ApiController {...}', FALSE, NOW(), NOW()),
    (3, 1, 1, 'ErrorResponse.java', 'BLOCK', 'Custom error response DTO', TRUE, NOW(), NOW()),

    -- 유저1 템플릿2 (template_id = 2) FILE 1개(모두 활성) (총 1개)
    (4, 2, 1, 'docker-compose.yml', 'FILE', 'version: "3" \nservices:\n  app:\n    image: myapp', FALSE, NOW(),
     NOW()),

    -- 유저2 템플릿1 (template_id = 3) BLOCK 2개(모두 활성) (총 2개)
    (5, 3, 2, 'redis-config.conf', 'BLOCK', 'maxmemory 512mb\nmaxmemory-policy allkeys-lru', FALSE, NOW(), NOW()),
    (6, 3, 2, 'cache-service.js', 'BLOCK', 'const cache = new RedisClient();', FALSE, NOW(), NOW()),

    -- 유저2 템플릿2 (template_id = 4) FILE 1개(활성) + BLOCK 1개(비활성) (총 2개)
    (7, 4, 2, 'oauth-login-flow.md', 'FILE', 'OAuth2 로그인 플로우 정리', FALSE, NOW(), NOW()),
    (8, 4, 2, 'security-config.java', 'BLOCK', '@Configuration public class SecurityConfig {...}', TRUE, NOW(),
     NOW()),

    -- 유저2 템플릿3 (template_id = 2) FILE 1개 + BLOCK 1개(모두 활성) (총 2개)
    (9, 5, 2, 'producer-config.properties', 'FILE', 'acks=all\nretries=3', FALSE, NOW(), NOW()),
    (10, 5, 2, 'consumer-service.java', 'BLOCK', 'public class Consumer {...}', FALSE, NOW(), NOW()),

    -- 유저3 템플릿1 (template_id = 6) FILE 2개(모두 활성) (총 2개)
    (11, 6, 3, 'useQueryExample.js', 'FILE', 'import { useQuery } from "react-query";', FALSE, NOW(), NOW()),
    (12, 6, 3, 'queryClientProvider.js', 'FILE', 'QueryClientProvider 설정 예시', FALSE, NOW(), NOW()),

    -- 유저3 템플릿2 (template_id = 7) BLOCK 1개(활성) + LOG 1개(비활성) (총 2개)
    (13, 7, 3, 'es-settings.json', 'BLOCK', '{ "analysis": { "analyzer": {...} } }', FALSE, NOW(), NOW()),
    (14, 7, 3, 'bulk-insert.sh', 'LOG', 'curl -XPOST localhost:9200/_bulk', TRUE, NOW(), NOW()),

    -- 유저3 템플릿3 (template_id = 8) BLOCK 1개(비활성) (총 1개)
    (15, 8, 3, 'deployment.yaml', 'BLOCK', 'apiVersion: apps/v1\nkind: Deployment', TRUE, NOW(), NOW());

-- 4. Prompts
INSERT INTO `prompt` (`prompt_id`, `parent_prompt_id`,
                      `template_id`, `notion_id`, `user_id`, `prompt_name`, `post_type`, `comment`,
                      `is_deleted`, `created_at`, `updated_at`)
VALUES
    -- 유저1 템플릿1 (template_id = 1)
    (1, NULL, 1, 1, 1, 'Spring Boot 글로벌 에러 처리', 'TECH_CONCEPT', 'ControllerAdvice를 이용한 통합 에러 핸들링', FALSE, NOW(), NOW()),
    (2, NULL, 1, 1, 1, 'Swagger 적용 방법', 'TROUBLE_SHOOTING', 'Springfox를 이용한 Swagger 문서화', FALSE, NOW(), NOW()),

    -- 유저1 템플릿2 (template_id = 2)
    (3, NULL, 2, 1, 1, 'Docker Compose 실습', 'TECH_CONCEPT', 'Docker 환경 설정과 멀티 컨테이너 구축', FALSE, NOW(), NOW()),

    -- 유저2 템플릿1 (template_id = 3)
    (4, NULL, 3, 1, 1, 'Redis Cache 전략', 'TECH_CONCEPT', 'TTL 설정과 캐시 무효화 처리', FALSE, NOW(), NOW()),

    -- 유저2 템플릿2 (template_id = 4)
    (5, NULL, 4, 2, 2, 'OAuth2 인증 실패 케이스 분석', 'TROUBLE_SHOOTING', 'Invalid token 문제 해결 방법', TRUE, NOW(), NOW()),

    -- 유저2 템플릿3 (template_id = 5)
    (6, NULL, 5, 2, 2, 'Kafka Consumer Rebalance 대응', 'TECH_CONCEPT', 'Partition Rebalance 대응 전략', FALSE, NOW(), NOW()),

    -- 유저3 템플릿1 (template_id = 6)
    (7, NULL, 6, 3, 3, 'React Query Devtools 사용법', 'TECH_CONCEPT', '쿼리 상태 모니터링 도구 소개', FALSE, NOW(), NOW()),

    -- 유저3 템플릿2 (template_id = 7)
    (8, NULL, 7, 3, 3, 'ElasticSearch 검색 성능 개선', 'TROUBLE_SHOOTING', 'Analyzer 최적화 및 색인 개선', FALSE, NOW(), NOW()),

    -- 유저3 템플릿3 (template_id = 8)
    (9, NULL, 8, 3, 3, 'Helm Chart 배포 자동화', 'TECH_CONCEPT', 'K8s Helm Chart 기본 구조 작성', TRUE, NOW(), NOW());

-- 5. Prompt_Attach
INSERT INTO `prompt_attach` (`attach_id`, `prompt_id`, `snapshot_id`, `description`, `is_deleted`, `created_at`,
                             `updated_at`)
VALUES
    -- 유저1 프롬프트1 (prompt_id = 1)
    (1, 1, 1, 'Spring Boot 글로벌 에러핸들러 예제 코드', FALSE, NOW(), NOW()),
    (2, 1, 3, 'ErrorResponse DTO 샘플', FALSE, NOW(), NOW()),

    -- 유저1 프롬프트2 (prompt_id = 2)
    (3, 2, 2, 'Swagger 설정 Controller 예시', FALSE, NOW(), NOW()),

    -- 유저1 프롬프트3 (prompt_id = 3)
    (4, 3, 4, 'docker-compose.yml 설정파일', FALSE, NOW(), NOW()),

    -- 유저2 프롬프트1 (prompt_id = 4)
    (5, 4, 5, 'Redis 캐시 정책 설정 예시', FALSE, NOW(), NOW()),
    (6, 4, 6, 'Cache 서비스 클라이언트 코드', FALSE, NOW(), NOW()),

    -- 유저2 프롬프트2 (prompt_id = 5)
    (7, 5, 7, 'OAuth 로그인 플로우 정리', FALSE, NOW(), NOW()),
    (8, 5, 8, 'Spring Security 설정 클래스', TRUE, NOW(), NOW()),

    -- 유저2 프롬프트3 (prompt_id = 6)
    (9, 6, 9, 'Kafka Producer 설정 파일', FALSE, NOW(), NOW()),
    (10, 6, 10, 'Consumer 서비스 코드', FALSE, NOW(), NOW()),

    -- 유저3 프롬프트1 (prompt_id = 7)
    (11, 7, 11, 'React Query useQuery 예시', FALSE, NOW(), NOW()),
    (12, 7, 12, 'QueryClientProvider 설정법', FALSE, NOW(), NOW()),

    -- 유저3 프롬프트2 (prompt_id = 8)
    (13, 8, 13, 'ElasticSearch Analyzer 설정', FALSE, NOW(), NOW()),
    (14, 8, 14, 'Bulk 색인 스크립트', TRUE, NOW(), NOW()),

    -- 유저3 프롬프트3 (prompt_id = 9)
    (15, 9, 15, 'Helm Chart Deployment 설정파일', TRUE, NOW(), NOW());
