
// css 파일 추가
// 1. 파일 경로 설정
const CSS_FILE_PATH = '/resources/css/boardList.css'
// 2. link 태그 생성
let linkEle = document.createElement("link");
linkEle.rel = 'stylesheet';
linkEle.type = 'text/css';
linkEle.href = CSS_FILE_PATH;
// 3. head 태그에 link 요소 추가
document.head.appendChild(linkEle);

// 새 게시글 등록 클릭 이벤트

document.querySelector('#registerBtn').addEventListener('click',()=>{
    location.href = '/board/register';
})











