// -----CSS 파일 추가
// 1. 파일 경로 설정
const CSS_FILE_PATH = ['/resources/css/get.css',
						'/resources/css/modal.css'];
// 2. link 태그 생성
CSS_FILE_PATH.forEach(css => {
	let linkEle = document.createElement("link");
	linkEle.rel = 'stylesheet';
	linkEle.type = 'text/css';
	linkEle.href = css;
	// 3. head 태그에 link 요소 추가
	document.head.appendChild(linkEle);
});

// 각 버튼 클릭 이벤트
document.querySelectorAll("button").forEach( btn => {
	btn.addEventListener('click', () => {
		
		let type = btn.getAttribute("id");
		
		if(type === 'indexBtn'){
			location.href = '/board/list';
		}else if(type === 'modifyBtn'){
			modify();
		}else if(type === 'replyBtn'){
			registerModalPage();
		}else if(type === 'closeModalBtn'){
			closeModal();
		}else if(type === 'addReplyBtn'){
			// 진짜 댓글 최종 등록 버튼
			registerReply();
		}else if(type === 'modifyReplyBtn'){
			// 진짜 댓글 최종 수정 버튼
			modifyReply();
		}else if(type === 'removeReplyBtn'){
			// 진짜 댓글 최종 삭제 버튼
			removeReply();
		}
	});
});

const f = document.forms[0];

function modify(){
	let bno;
	// 1. form 객체에서 name 속성 데이터 가져오는 방법
	bno = f.bno.value;
	
	// 2. URLSearchParams 객체 이용
	bno = new URLSearchParams(location.search).get("bno");
	
	location.href = '/board/modify?bno=' + bno;
}


//----------- 댓글 관련 스크립트------------
const rs = replyService; // reply.js에서 CRUD 담당하는 객체

showList();
function showList(){
	let bno = f.bno.value;
	let replyUL = document.querySelector('.chat');
	
	rs.getList(bno, jsonArray => {
		let msg = '';

		jsonArray.forEach(reply => {
			msg += `<li data-rno="${reply.rno}" onclick="modifyModalPage(this)">`;
			msg += 		`<div>`;
			msg += 			`<div class="chat-header">`;
			msg += 				`<strong>${reply.replyer}</strong>`;
			msg += 				`<small class="pull-right">${displayTime(reply.replydate)}</small>`;
			msg += 			`</div>`;
			msg += 			`<p>${reply.reply}</p>`;
			msg += 		`</div>`;
			msg += `</li>`;
			
		});
		
		replyUL.innerHTML = msg;
	});
	
}


// unixTimeStamp to date
function displayTime(unixTimeStamp){
	let myDate = new Date(unixTimeStamp);
	
	let y = myDate.getFullYear();
	let m = String(myDate.getMonth()+1).padStart(2, '0');
	let d = String(myDate.getDate()).padStart(2, '0');
	
	let date = `${y}-${m}-${d}`;
	return date;
}


//----------- 모달 관련 스크립트------------
const modal = document.querySelector("#modal");
const inputReply = document.querySelector("input[name=reply]");
const inputReplyer = document.querySelector("input[name=replyer]");
const inputReplydate = document.querySelector("input[name=replydate]");
const addReplyBtn = document.querySelector("#addReplyBtn");
const modifyReplyBtn = document.querySelector("#modifyReplyBtn");
const removeReplyBtn = document.querySelector("#removeReplyBtn");

function openModal(){
	modal.style.display = 'block';
	document.body.style.overflow = 'hidden';
}
function closeModal(){
	modal.style.display = 'none';
	document.body.style.overflow = 'auto';
}

// 댓글 등록 창 함수
function registerModalPage(){
	// 보여질 목록 수정
	regReplyModalStyle();
	// 입력 내용 초기화&불러오기
	inputReply.value = '';
	inputReplyer.value = '';
	// 모달 창 열기
	openModal();
}
// 댓글 달기 창 스타일 변경 함수
function regReplyModalStyle(){
	modifyReplyBtn.classList.add('hide');
	removeReplyBtn.classList.add('hide');
	inputReplydate.closest('div').classList.add('hide');
	addReplyBtn.classList.remove('hide');
	inputReplyer.removeAttribute('readonly');
}
// 진짜 댓글 삽입 함수
function registerReply(){
	// 1. 댓글 삽입
	// 2. 모달 창 해제
	// 3. 댓글 목록 다시 불러오기
	
	// 댓글 인풋들 데이터 검증
	if(!inputReply.value || !inputReplyer.value){
		alert("모든 내용을 입력하세요");
		return;
	}
	
	rs.add(
		{
			bno : f.bno.value,
			reply : inputReply.value,
			replyer : inputReplyer.value
		}, 
		function(result){
			console.log("result : " + result);
			closeModal();
			showList();
		}	
	);
}


// 댓글 수정 창 함수
let rno;
function modifyModalPage(li){
	// 모달 창 열기
	openModal();
	
	// 보여질 목록 수정
	modReplyModalStyle();
	
	// 입력 내용 초기화 & 불러오기
	rno = li.getAttribute("data-rno");
	
	// li 태그에서 값을 꺼내서 각 인풋에 바인딩
	inputReply.value = li.querySelector('p').innerText;
	inputReplyer.value = li.querySelector('strong').innerText;
	inputReplydate.value = li.querySelector('small').innerText;
}
function modReplyModalStyle(){
	addReplyBtn.classList.add('hide');
	modifyReplyBtn.classList.remove('hide');
	removeReplyBtn.classList.remove('hide');
	inputReplydate.closest('div').classList.remove('hide');
	inputReplyer.setAttribute('readonly', true);
	inputReplydate.setAttribute('readonly', true);
}


// 1. 댓글 수정(내용 값 검증), 삭제(댓글 삭제할꺼냐 물어보기)
// -> 적용 뒤에 모달 닫아주고, 목록 가져오기
// 2. 댓글 달기 > 댓글 수정 > 댓글 달기 스타일 수정


// 진짜 댓글 수정 함수
function modifyReply(){
	if(!inputReply.value){
		alert('수정할 내용을 입력하세요');
		return;
	}
	
	rs.update(
		{
			rno : rno,
			reply : inputReply.value
		}
		, result => {
			console.log(result);
			closeModal();
			showList();
		}
	);
	
}
// 진짜 댓글 삭제 함수
function removeReply(){
	if(confirm('댓글을 삭제하시겠습니까?')){
		rs.remove(rno, result => {
			console.log(result);
			closeModal();
			showList();
		});
	}
}

// -------------첨부 파일 관련 스크립트 -------------------
(function(){
	
	fetch(`/board/getAttachList/${f.bno.value}`)
		.then(response => response.json())
		.then(result => {
			console.log(result);
			showUploadedFile(result);
		})
		.catch(err => console.log(err));
	
})();

//전달 받은 파일 정보 화면 출력 함수
let uploadResult = document.querySelector(".uploadResult ul");
function showUploadedFile(uploadResultArr){
	let str = ``;
	uploadResultArr.forEach( file => {
		const {uploadPath, uuid, fileName} = file;
		let fileCallPath = 
			encodeURIComponent(`${uploadPath}/${uuid}_${fileName}`);
		str += `<li path="${uploadPath}" uuid="${uuid}" fileName="${fileName}">`;
		str += `<a href="/download?fileName=${fileCallPath}">`;
		//str += `<a>`;
		str += `${file.fileName}`;
		str += `</a>`;
		str += `</li>`;
		
	});
	uploadResult.innerHTML = str;
}














