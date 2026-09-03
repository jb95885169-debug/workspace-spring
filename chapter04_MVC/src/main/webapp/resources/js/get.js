// ------ CSS 파일 추가
// 1. 파일 경로 설정
const CSS_FILE_PATH = ['/resources/css/get.css',
						'/resources/css/modal.css'];
// 2. link 태그 생성
CSS_FILE_PATH.forEach(css => {						// << 두가지 이상 css 적용시키는방법
	
	let linkEle = document.createElement("link");
	linkEle.rel = 'stylesheet';
	linkEle.type = 'text/css';
	linkEle.href = css;
// 3. head 태그에 link 요소 추가
	document.head.appendChild(linkEle);
	
});
// 각 버튼 클릭 이벤트
// 수정 버튼 클릭 시 - modify 함수 실행
document.querySelectorAll("button").forEach(btn => {
    btn.addEventListener('click', ()=>{
        let type = btn.getAttribute("id");

        if(type === 'indexBtn'){
            location.href = '/board/list'
        }else if(type === 'modifyBtn'){
        	console.log("수정버튼");
        	modify();
        }else if(type === 'replyBtn'){
            registerModalPage();
        }else if(type === 'closeModalBtn'){
            closeModal();
        }else if(type === 'addReplyBtn'){
            // 진짜 댓글 최종 등록 버튼
            registerReply();
        }else if(type === 'modifyReplyBtn'){
        	modifyReply();
        }else if(type === 'removeReplyBtn'){
        	removeReply();
        }
    });
});
const f = document.forms[0]
// modify 함수 - /board/modify?bno=bno 로 서블릿 요청
function modify(){
   //let bno;
   // 1. form 객체에서 name 속성 데이터 가져오는 방법
	const bno = f.bno.value;
   
   // 2. URLSearchParams 객체 이용
   
//   bno = new URLSearchParams(location.serach).get("bno");
   location.href = '/board/modify?bno=' + bno;
}

// -----------------------------댓글 관련 스크립트--------------

const rs = replyService; // reply.js에서 CRUD 담당하는 객체

showList();
function showList() {
	let bno = f.bno.value;
    let replyUL = document.querySelector('.chat');
    
    rs.getList(bno, jsonArray => {
        console.log(jsonArray);
        let msg = '';

        jsonArray.forEach(reply =>{
            msg += `<li data-rno="${reply.rno}" onclick = "modifyModalPage(this)">`;
            msg +=      `<div>`;
            msg +=          `<div class="chat-header">`;
            msg +=              `<strong>${reply.replyer}</strong>`;
            msg +=              `<small class="pull-right">${displayTime(reply.replydate)}</small>`;
            msg +=      `</div>`;
            msg +=      `<p>${reply.reply}</p>`;
            msg +=  `</div>`;
            msg += `</li>`;
        });


        replyUL.innerHTML = msg;
	});

}


// 
function displayTime(unixTimeStamp){
    let myDate = new Date(unixTimeStamp);

    let y = myDate.getFullYear();
    let m = String(myDate.getMonth()+1).padStart(2,'0');
    let d = String(myDate.getDate()).padStart(2,'0');

    let date = `${y}-${m}-${d}`;
    return date;
}











// 댓글추가
//rs.add(
//    {
//    	bno : f.bno.value,
//    	reply : 'JS TEST',
//    	replyer : 'JS TESTER'
//    },
//    function(result){
//		alert(result);
//    }
//);

//rs.getList(f.bno.value, function(result) {
//	console.log(result);
//	
//	
//});


//rs.remove(10 ,result =>{
//	console.log(result);
//});



//rs.update({
//		rno : 2844,
//		reply : '수정 테스트중 ...'
//	},result =>{
//		console.log(result);
//	});




//rs.get(2844, result=>{
//	console.log(result);
//});


//------------모달 관련 스크립트---------------

const modal = document.querySelector("#modal");
const inputReply = document.querySelector("input[name=reply]");
const inputReplyer = document.querySelector("input[name=replyer]");
const inputReplydate = document.querySelector("input[name=replydate]");
const addReplyBtn = document.querySelector("#addReplyBtn");
const modifyReplyBtn = document.querySelector("#modifyReplyBtn");
const removeReplyBtn = document.querySelector("#removeReplyBtn");
// -----페이지 수정 모달------
//const modifyReplyBtn = document.querySelector("#modifyReplyBtn");



function openModal(){
    modal.style.display = 'block';
}

function closeModal(){
    modal.style.display = 'none';
    // document.body.style.overflow = 'hidden'; //  'auto';	// 모달창이 열렸을때 스크롤이 안움직이게 하는 코드

}


// 댓글 등록창 함수
function registerModalPage() {
	// 보여질 목록 수정
	regReplyModalStyle()
	// 입력 내용 초기화&불러오기
	inputReply.value = '';
    inputReplyer.value = '';
	// 모달 창 열기
	openModal();
//	inputReplyer.disabled = false;
	
}

// 댓글 달기 창 스타일 변경 함수
function regReplyModalStyle() {
    modifyReplyBtn.classList.add('hide');
    removeReplyBtn.classList.add('hide');
    inputReplydate.closest('div').classList.add('hide');
}


// 진짜 댓글 삽입 함수
function registerReply(){
    // 1. 댓글 삽입
    // 2. 모달창 해제
    // 3. 댓글 목록 다시 불러오기


    // 댓글 input들 데이터 검증
    if(inputReply.value == '' || inputReplyer.value == ''){
        alert("내용을 입력해주세요")
        return;
    }
    
    // 댓글 삽입 후 result 값 콘솔에 출력
    rs.add(
       {
       	bno : f.bno.value,
       	reply : inputReply.value,
       	replyer : inputReplyer.value
       },

       function(result){
//    		console.log(result);
    	    // 2. 모달 창 해제
    		closeModal();
    	    // 3.댓글 목록 다시 불러오기
    		showList();
       }
    );
    // console.log(f.bno.value);
    // ----------위에 까지가 댓글 삽입
}
    
    
// 댓글 수정 창 함수
let rno;
function modifyModalPage(li) {
	// 모달 창 열기
	openModal();
	// 보여질 목록 수정
	modReplyModalStyle();
	// 입력 내용 초기화& 불러오기
	rno = li.getAttribute("data-rno",li);
	// li 태그에서 값을 꺼내서 각 인풋에 바인딩
	inputReply.value = li.querySelector('p').innerText;
	inputReplyer.value = li.querySelector('strong').innerText;
    inputReplydate.value = li.querySelector('small').innerText;
	
	
	
	
}

function modReplyModalStyle(){
	// 등록 버튼 숨기기
	// 수정 & 삭제 버튼 보여주기
//	addReplyBtn.classList.add('hide');
	modifyReplyBtn.classList.remove('hide');
	removeReplyBtn.classList.remove('hide');
	inputReplydate.closest('div').classList.remove('hide');
	// 등록 날짜 데이터 보여주기
	
	// 등록 날짜, 작성자 수정 불가(읽기 전용)
//	inputReplyer.setAttribute('readonly', true);
//	inputReplydate.setAttribute('readonly', true);
	
	addReplyBtn.classList.remove('hide');
	inputReplyer.removeAttribute('readonly');
}

// 댓글 수정(내용 값 검증 ), 삭제(댓글 삭제 확인 메세지)
function modifyReply(){
        if(inputReply.value == ''){
        alert("수정할 내용을 입력해주세요")
        return;
    }
    // 댓글 삽입 후 result 값 콘솔에 출력
   rs.update({
		rno : rno,
		reply : inputReply.value,
	},result =>{
		console.log(result);
            closeModal();
    		showList();
	});
        
        
        
        
}

// ->적용 뒤에 모달 닫아주고, 목록 가져오기  closeModal(); showList();
// 댓글 달기 > 댓글 수정 > 댓글 달기 스타일 수정  

// 진짜 댓글 삭제 함수
function removeReply() {
	if(confirm('댓글을 삭제하시겠습니까?')){
        rs.remove(rno,result =>{
            closeModal();
    		showList();
        });

    }
    
}

