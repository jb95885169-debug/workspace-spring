// ------ CSS 파일 추가
// 1. 파일 경로 설정
const CSS_FILE_PATH = '/resources/css/get.css';
// 2. link 태그 생성
let linkEle = document.createElement("link");
linkEle.rel = 'stylesheet';
linkEle.type = 'text/css';
linkEle.href = CSS_FILE_PATH;
// 3. head 태그에  link 요소 추가
document.head.appendChild(linkEle);

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
//});

re.remove(f.bno.value, function(result) {
	console.log(result);
	});















