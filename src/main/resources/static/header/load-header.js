fetch('/header/header.html')
    .then(res => res.text())
    .then(html => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        document.getElementById('header-placeholder').appendChild(doc.querySelector('header'));
    });
