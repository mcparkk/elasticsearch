
## kibana dev_tools sample


- PUT /content
```json
{
  "mappings": {
    "properties": {
      "id":        { "type": "keyword" },
      "title":     { "type": "text" },
      "tags":      { "type": "keyword" },
      "describe":  { "type": "text" },
      "title_suggest": {
        "type": "completion"
      }
    }
  }
}
```

```json
{
  "settings": {
    "number_of_shards": 2,
    "number_of_replicas": 0,
    "index.max_ngram_diff": 19,
    "analysis": {
      "tokenizer": {
        "autocomplete_tokenizer": {
          "type": "edge_ngram",
          "min_gram": 2,
          "max_gram": 20,
          "token_chars": ["letter", "digit", "whitespace", "punctuation"]
        }
      },
      "analyzer": {
        "autocomplete_ko": {
          "type": "custom",
          "tokenizer": "autocomplete_tokenizer",
          "filter": ["lowercase", "stop"],
          "char_filter": ["html_strip"]
        },
        "nori_ko": {
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
      "title": {
        "type": "text",
        "analyzer": "nori_ko",
        "copy_to": "combined"
      },
      "tags": {
        "type": "text",
        "analyzer": "nori_ko",
        "copy_to": "combined"
      },
      "describe": {
        "type": "text",
        "analyzer": "nori_ko",
        "copy_to": "combined"
      },
      "combined": {
        "type": "text",
        "analyzer": "nori_ko",
        "fields": {
          "autocomplete": {
            "type": "text",
            "analyzer": "autocomplete_ko",
            "search_analyzer": "standard"
          }
        }
      }
    }
  }
}

```
- Index Settings 
  - `number_of_shards`: 인덱스를 2개의 샤드로 분할하여 분산 저장 및 검색 성능 향상.
  - `number_of_replicas`: 각 샤드에 대해 0개의 복제본을 유지하여 고가용성 확보.(현재 싱글노드)
  - `index.max_ngram_diff`: edge_ngram 토크나이저에서 max_gram - min_gram의 최대 차이를 19 로 설정
---
- Analysis 설정 
  - `edge_ngram`: 자동완성 기능을 위한 토크나이저로, 단어의 앞부분부터 일정 길이까지 토큰화.
  - `token_chars`: 토큰화에 포함할 문자 유형 지정 (문자, 숫자, 공백, 구두점 포함).
---
- Analyzer 
  - `autocomplete_ko`: 자동완성용 분석기로, edge_ngram 토크나이저와 소문자 필터 사용.
  - `nori_ko`: 한국어 형태소 분석기인 nori_tokenizer를 사용하여 텍스트를 분석하고 소문자 필터 적용.
---
- Mappings 설정
  - `title` / `tags` / `describe` (text, analyzer: `nori_ko`): 한국어 형태소 분석을 통해 텍스트 필드 분석. combined 필드로 복사되어 통합 검색 가능.
  - `combined` (text, analyzer: `nori_ko`): title, tags, describe 필드의 내용을 통합하여 검색 성능 향상.
    - combined.autocomplete (text, analyzer: autocomplete_ko, search_analyzer: standard): 자동완성 기능을 위한 서브 필드. 입력 시 edge_ngram으로 분석하고, 검색 시에는 표준 분석기 사용.