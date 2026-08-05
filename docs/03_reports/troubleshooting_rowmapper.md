# RowMapper 트러블 슈팅

## 📌 문제 발생

**문제 유형 (핵심 태그):**  데이터 불일치

- **어떤 기능에서 문제가 발생했는가? / 문제 상황:**
    - post Repository에서 RowMapper 에  RecipePost는 categoryName을 사용하기에 RowMapper에서 매핑을 시켰는데 TipPost에서는 categoryName 컬럼이 존재하지 않기에 해당 TipPost에서 sql query에서  categoryName이 null값이라는 문제가 발생했습니다.

---

## 🔍 원인 분석

- **왜 발생했는가? / 어떤 코드/구조에서 문제가 있었는가?**
    - TipPost에서는 categoryName을 컬럼이 존재하지 않음
    - RowMapper 코드에 문제 발생

---

## 🛠 해결 방법

- **어떻게 해결했는가?**
    - categoryName컬럼이 존재하지 않으면 null을 반환하도록 해서 해결했습니다.

    <aside>
    👉

  `.categoryName(hasColumn(rs, "category_name") ? rs.getString("category_name") : null)`

    </aside>


---

## 🧠 배운점

- RowMapper에 매핑을 했다면 다른 테이블에서도 쓰는지 여부를 확인해야하고 쓰지 않는다면 예외처리를 해야한다는 것을 배웠습니다.