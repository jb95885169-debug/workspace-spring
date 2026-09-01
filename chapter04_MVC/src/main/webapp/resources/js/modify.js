const param = location.pathname;
console.log(param)



// css 파일 추가
// 1. 파일 경로 설정
const CSS_FILE_PATH = '/resources/css/modify.css'
// 2. link 태그 생성
let linkEle = document.createElement("link");
linkEle.rel = 'stylesheet';
linkEle.type = 'text/css';
linkEle.href = CSS_FILE_PATH;
// 3. head 태그에 link 요소 추가
document.head.appendChild(linkEle);

// 버튼 클릭 - 수정 버튼 (modify 함수), 삭제 버튼 (remove 함수)
document.querySelectorAll("button").forEach(btn => {
    btn.addEventListener('click', ()=>{
        let type = btn.getAttribute("id");

        if(type === 'indexBtn'){
            location.href = '/board/list'
        }else if(type === 'modifyBtn'){
        	console.log("수정버튼");
        	modify();
        }else if(type === 'removeBtn'){
        	console.log("삭제버튼");
        	remove();
        }
    });
});
// modify 함수
// 제목, 내용 빈 값 검증 후 전송
const f = document.forms[0];

function modify(){
    if(!f.title.value){
        alert('제목을 입력하세요')
        return;
    }
    if(!f.content.value){
        alert('내용을 입력하세요')
        return;
    }

    f.action = '/board/modify';
    f.submit();

}

// remove 함수
// 글삭제 여부 물어보고 삭제 진행
function remove(){
//	const bno = document.forms[0].bno.value;
	if(confirm('정말로 게시글을 삭제하시겠습니까?')){
        let bnoEle = f.bno; // bno 담고있는 input 요소
        f.innerHTML = '';   // form 태그 내부 비우기
        f.appendChild(bnoEle);  // form 태그 내부에 bno요소 추가

        f.action = '/board/remove';
        f.submit(); // submit() 함수로 만들었어야 하는데 submit 으로 만들어서 오류
    }
}

// 각 버튼 클릭 이벤트
// 수정 버튼 클릭 시 - modify 함수 실행











