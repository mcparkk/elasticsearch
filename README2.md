# Elasticsearch 한글 N-gram 검색 및 자동완성 가이드

이 문서는 Elasticsearch에서 한글 초성 검색과 자동완성을 구현하는 방법을 설명합니다.

## 주요 특징

### 1. **Combined 필드 통합 검색**
- `title`, `tags`, `describe` 필드가 모두 `combined` 필드로 복사
- 하나의 필드에서 통합 검색 가능
- `copy_to` 기능 활용

### 2. **N-gram 토크나이저**
- `edge_ngram`이 아닌 `ngram` 사용
- 단어의 어느 위치에서든 매칭 가능
- "ㄱ" 입력 시 "검색", "가이드" 등 모든 "ㄱ" 포함 단어 검색

### 3. **한글 초성 검색**
- 한글 자음/모음 단위로 검색 가능
- 부분 일치 검색 지원

## 인덱스 설계

### Elasticsearch DevTools용 인덱스 생성 JSON

```json
PUT /content
{
  "settings": {
    "number_of_shards": 2,
    "number_of_replicas": 0,
    "index.max_ngram_diff": 19,
    "analysis": {
      "tokenizer": {
        "ngram_tokenizer": {
          "type": "ngram",
          "min_gram": 1,
          "max_gram": 10,
          "token_chars": ["letter", "digit", "whitespace", "punctuation"]
        }
      },
      "analyzer": {
        "ngram_analyzer": {
          "type": "custom",
          "tokenizer": "ngram_tokenizer",
          "filter": ["lowercase", "stop"],
          "char_filter": ["html_strip"]
        },
        "nori_ko": {
          "type": "custom",
          "tokenizer": "nori_tokenizer",
          "filter": ["lowercase", "stop"],
          "char_filter": ["html_strip"]
        },
        "search_analyzer": {
          "type": "custom",
          "tokenizer": "nori_tokenizer",
          "filter": ["lowercase", "stop"],
          "char_filter": ["html_strip"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "id": {
        "type": "keyword"
      },
      "title": {
        "type": "text",
        "analyzer": "nori_ko",
        "copy_to": "combined"
      },
      "tags": {
        "type": "text",
        "analyzer": "nori_ko",
        "copy_to": "combined",
        "fields": {
          "keyword": {
            "type": "keyword"
          }
        }
      },
      "describe": {
        "type": "text",
        "analyzer": "nori_ko",
        "copy_to": "combined"
      },
      "created_at": {
        "type": "date"
      },
      "updated_at": {
        "type": "date"
      },
      "combined": {
        "type": "text",
        "analyzer": "nori_ko",
        "fields": {
          "ngram": {
            "type": "text",
            "analyzer": "ngram_analyzer",
            "search_analyzer": "standard"
          },
          "search": {
            "type": "text",
            "analyzer": "search_analyzer"
          }
        }
      },
      "title_suggest": {
        "type": "completion",
        "analyzer": "nori_ko",
        "preserve_separators": true,
        "preserve_position_increments": true,
        "max_input_length": 50
      },
      "tag_suggest": {
        "type": "completion",
        "analyzer": "nori_ko",
        "preserve_separators": true,
        "preserve_position_increments": true,
        "max_input_length": 50
      }
    }
  }
}
```

## 필드 구조 설명

### 1. **기본 필드**
- `id`: 문서 식별자 (keyword)
- `title`: 제목 (한글 형태소 분석 + combined로 복사)
- `tags`: 태그 (한글 형태소 분석 + combined로 복사)
- `describe`: 설명 (한글 형태소 분석 + combined로 복사)
- `created_at`, `updated_at`: 타임스탬프

### 2. **Combined 필드**
```json
"combined": {
  "type": "text",
  "analyzer": "nori_ko",           // 한글 형태소 분석
  "fields": {
    "ngram": {                     // ngram 자동완성용
      "type": "text",
      "analyzer": "ngram_analyzer",
      "search_analyzer": "standard"
    },
    "search": {                    // 검색용
      "type": "text", 
      "analyzer": "search_analyzer"
    }
  }
}
```

### 3. **자동완성 필드**
- `title_suggest`: 제목 자동완성 (Completion Suggester)
- `tag_suggest`: 태그 자동완성 (Completion Suggester)

## 분석기 설정

### 1. **N-gram 토크나이저**
```json
"ngram_tokenizer": {
  "type": "ngram",
  "min_gram": 1,
  "max_gram": 10,
  "token_chars": ["letter", "digit", "whitespace", "punctuation"]
}
```

### 2. **N-gram 분석기**
```json
"ngram_analyzer": {
  "type": "custom",
  "tokenizer": "ngram_tokenizer",
  "filter": ["lowercase", "stop"],
  "char_filter": ["html_strip"]
}
```

### 3. **한글 분석기**
```json
"nori_ko": {
  "type": "custom",
  "tokenizer": "nori_tokenizer",
  "filter": ["lowercase", "stop"],
  "char_filter": ["html_strip"]
}
```

## 샘플 데이터

### 테스트용 문서 생성
```json
POST /content/_doc/1
{
  "id": "1",
  "title": "Elasticsearch 한글 검색 가이드",
  "tags": ["elasticsearch", "검색", "한글"],
  "describe": "Elasticsearch에서 한글 검색을 구현하는 방법에 대한 상세한 가이드입니다.",
  "created_at": "2024-01-01T00:00:00",
  "updated_at": "2024-01-01T00:00:00"
}
```

## 검색 테스트

### 1. **통합 검색 (combined 필드)**
```json
GET /content/_search
{
  "query": {
    "match": {
      "combined.search": "한글 검색"
    }
  }
}
```

### 2. **N-gram 자동완성 (ㄱ 입력 시)**
```json
GET /content/_search
{
  "query": {
    "match": {
      "combined.ngram": "ㄱ"
    }
  },
  "size": 5
}
```

### 3. **Completion Suggester**
```json
GET /content/_search
{
  "suggest": {
    "title_suggest": {
      "prefix": "한글",
      "completion": {
        "field": "title_suggest",
        "size": 5
      }
    }
  }
}
```

### 4. **태그 자동완성**
```json
GET /content/_search
{
  "suggest": {
    "tag_suggest": {
      "prefix": "elastic",
      "completion": {
        "field": "tag_suggest",
        "size": 5
      }
    }
  }
}
```

## 검색 결과 예시

### N-gram 검색 결과 ("ㄱ" 입력 시)
```json
{
  "hits": {
    "total": {
      "value": 1,
      "relation": "eq"
    },
    "hits": [
      {
        "_source": {
          "title": "Elasticsearch 한글 검색 가이드",
          "tags": ["elasticsearch", "검색", "한글"],
          "describe": "Elasticsearch에서 한글 검색을 구현하는 방법에 대한 상세한 가이드입니다."
        }
      }
    ]
  }
}
```

## 성능 최적화

### 1. **인덱스 설정**
- `number_of_shards`: 2 (분산 처리)
- `number_of_replicas`: 0 (개발 환경)
- `index.max_ngram_diff`: 19 (ngram 최대 차이)

### 2. **토크나이저 설정**
- `min_gram`: 1 (최소 1글자)
- `max_gram`: 10 (최대 10글자)
- `token_chars`: 문자, 숫자, 공백, 구두점 포함

### 3. **분석기 최적화**
- `lowercase`: 소문자 변환
- `stop`: 불용어 제거
- `html_strip`: HTML 태그 제거

## 주의사항

1. **Nori 플러그인**: Elasticsearch에 analysis-nori 플러그인 설치 필요
2. **메모리 사용량**: ngram 토크나이저는 인덱스 크기를 증가시킬 수 있음
3. **검색 성능**: ngram 필드는 검색 속도에 영향을 줄 수 있음
4. **한글 처리**: 한글 자음/모음 분리 시 정확한 매칭 필요

## 사용 시나리오

### 1. **한글 초성 검색**
- 사용자가 "ㄱ" 입력 → "검색", "가이드" 등 매칭
- 부분 일치 검색으로 사용자 편의성 향상

### 2. **통합 검색**
- 제목, 태그, 설명에서 동시 검색
- 하나의 쿼리로 모든 필드 검색 가능

### 3. **자동완성**
- Completion Suggester로 빠른 제안
- N-gram으로 부분 일치 자동완성

이 설계를 통해 한글 초성 검색과 자동완성이 모두 지원되는 완전한 검색 시스템을 구축할 수 있습니다! 🎉 