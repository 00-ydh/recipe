# TUI Editor - 헤더 검색 form 충돌로 인한  요리꿀팁 작성 오류

## 📌 문제 발생

1. **문제 유형 (핵심 태그)**
    - 폼 처리
    - 이벤트 처리
2. **어떤 기능에서 문제가 발생했는가?**
    - 요리 꿀팁 작성 페이지(`/tip/write`)에서 Toast UI Editor로 내용을 입력하고 등록 버튼을 눌렀을 때, 에디터에 작성한 내용이 서버로 전달되지 않고 content 값이 빈 값으로 저장됨

---

## 🔍 원인 분석

- **왜 발생했는가?**
    - Toast UI Editor는 일반 `<textarea>`가 아닌 별도 에디터 영역에 내용을 입력받기 때문에, form 제출 직전에 JavaScript로 에디터 내용을 hidden input(`#content`)에 담아주는 작업이 필요함
    - 이를 위해 `document.querySelector('form')`으로 form을 선택해 submit 이벤트를 등록했는데, 공통 헤더에 검색창 `<form>`이 존재해 **페이지에 form이 2개**인 상황이었음
- **어떤 코드/구조에서 문제가 있었는가?**
    - `querySelector('form')`은 DOM에서 첫 번째로 발견되는 form을 반환하기 때문에, 작성 폼이 아닌 **헤더 검색 폼**에 submit 이벤트가 걸려버림
    - 결과적으로 등록 버튼을 눌러도 에디터 내용이 hidden input에 담기지 않아 서버에 빈 값이 전달됨

---

## 🛠 해결 방법

- 작성 폼에 클래스명(`write-box`)을 지정하고, 선택자를 구체적으로 변경

```jsx
// 수정 전 (문제) - 헤더 검색 form을 잘못 선택
const form = document.querySelector('form');

// 수정 후 (해결) - 클래스명으로 작성 폼만 정확히 선택
const form = document.querySelector('form.write-box');

form.addEventListener('submit', function() {
    document.querySelector("#content").value = editor.getHTML();
});
```

---

## 🧠 배운점

- **`querySelector`의 동작 방식 이해**: `querySelector('form')`은 DOM에서 가장 먼저 발견되는 요소를 반환하기 때문에, 동일한 태그가 여러 개 있을 경우 의도하지 않은 요소가 선택될 수 있다는 것을 직접 경험으로 배움
- **공통 컴포넌트 충돌 주의**: 헤더처럼 모든 페이지에 공통으로 삽입되는 컴포넌트에 `<form>` 태그가 포함된 경우, 페이지 내 JavaScript 선택자와 충돌할 수 있음
- **선택자는 항상 구체적으로**: 태그명만으로 선택하는 것보다 클래스나 id를 활용해 명확하게 대상을 지정하는 습관이 중요하다는 것을 깨달음
- **외부 라이브러리 활용법 습득**: Toast UI Editor를 직접 프로젝트에 적용하면서, CDN으로 라이브러리를 불러오는 방법, 에디터 초기화 옵션 설정, 이미지 업로드 훅(hook) 연동, 폼 제출 시 에디터 내용을 추출하는 방법 등 외부 라이브러리를 실제 서비스에 통합하는 전반적인 흐름을 배움