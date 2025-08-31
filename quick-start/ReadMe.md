1. ollama serve option
* ollama pull nomic-embed-text + ollama pull llama3.1
2. docker compose up -d
3. data/ 폴더에 문서 넣고 앱 재시작 → 자동 ETL
4. request
```shell
curl -X GET --location "http://localhost:8080/ask?q=가장 최근 이력이 뭐야?"
```
