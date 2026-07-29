// header.css 자동 주입
const headerLink = document.createElement('link');
headerLink.rel = 'stylesheet';
headerLink.href = '/header/header.css';
document.head.appendChild(headerLink);

// header.html 로드
fetch('/header/header.html')
    .then(res => res.text())
    .then(html => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        document.getElementById('header-placeholder').appendChild(doc.querySelector('header'));
        // header.html 내 script 실행
        const script = doc.querySelector('script');
        if (script) eval(script.textContent);
    });
