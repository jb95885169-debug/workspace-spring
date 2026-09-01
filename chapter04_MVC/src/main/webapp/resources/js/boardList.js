
// css 파일 추가
// 1. 파일 경로 설정
const CSS_FILE_PATH = ['/resources/css/boardList.css',
						'/resources/css/page.css'];
// 2. link 태그 생성
CSS_FILE_PATH.forEach(css => {
	
	let linkEle = document.createElement("link");
	linkEle.rel = 'stylesheet';
	linkEle.type = 'text/css';
	linkEle.href = css;
// 3. head 태그에 link 요소 추가
	document.head.appendChild(linkEle);
	
});


// 새 게시글 등록 클릭 이벤트

document.querySelector('#registerBtn').addEventListener('click',()=>{
	location.href = '/board/register';
})

// 제목 클릭 이벤트 ( get 이동 )

document.querySelectorAll('tbody a').forEach(a =>{
	a.addEventListener('click', e =>{
        e.preventDefault();

        const bno = a.getAttribute("href");

        location.href = '/board/get?bno=' + bno;
    });
});

// ----------페이징 처리 관련 코드
let pageNum = new URLSearchParams(location.search).get('pageNum');
let amount = new URLSearchParams(location.search).get('amount');
if(!pageNum || !amount){
	pageNum = 1;
	amount = 10;
}
setStorageData(pageNum, amount);




// 페이지 버튼 클릭 이벤트 - 
document.querySelectorAll(".page-nation li a").forEach(aEle =>{
	aEle.addEventListener('click', e=> {
		e.preventDefault();
		
		pageNum = e.target.getAttribute('href');
		amount = 10;
		
		
		sendData = `pageNum=${pageNum}&amount=${amount}`;
		location.href = `/board/list?pageNum=${pageNum}&amount=${amount}`;
		
	});
});




















