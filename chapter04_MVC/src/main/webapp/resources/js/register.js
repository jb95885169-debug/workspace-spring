
// css 파일 추가
// 1. 파일 경로 설정
const CSS_FILE_PATH = '/resources/css/register.css'
// 2. link 태그 생성
let linkEle = document.createElement("link");
linkEle.rel = 'stylesheet';
linkEle.type = 'text/css';
linkEle.href = CSS_FILE_PATH;
// 3. head 태그에 link 요소 추가
document.head.appendChild(linkEle);

// 새 게시글 등록 클릭 이벤트
const f = document.forms[0];	// form객체

// 각 버튼 클릭 이벤트
// 눌려진 버튼의 id에 따라서 행동 분기
// 새 게시글 등록 > register()
// 다시 작성 > form 리셋
// 목곡으로 이동 > 게시글 목록 이동

// register() - 제목 ,작성자, 내용 빈 값 검증 후 데이터 전송

document.querySelectorAll("button").forEach( btn =>{
    btn.addEventListener('click',()=>{

        let type = btn.getAttribute("id");

        console.log(type);
        
        if (type === 'indexBtn') {
        	const {pageNum, amount} = getStorageData();
        	const sendData = `pageNum=${pageNum}&amount=${amount}`;
            location.href = '/board/list?' + sendData;
        }else if(type === 'resetBtn'){
            f.reset();
        }else if(type === 'registerBtn'){
            register();
        }


    })
})
// 게시글 등록 함수
function register(){
    if(f.title.value == ''){
        alert('제목을 입력하세요')
        return;
    }
    if(f.writer.value == ''){
        alert('작성자을 입력하세요')
        return;
    }
    if(f.content.value == ''){
        alert('내용을 입력하세요')
        return;
    }

    
    // 첨부 파일 정보 가져와서 form에 담기
    let str = ``;
    document.querySelectorAll(`.uploadResult ul li`).forEach((li, index) =>{
    	let path = li.getAttribute("path");
    	let uuid = li.getAttribute("uuid");
    	let fileName = li.getAttribute("fileName");
    	
    	str += ` <input type="hidden"`;
    	str += ` name="attachList[${index}].fileName" `;
    	str += ` value="${fileName}"/>`;
    	
    	str += ` <input type= "hidden"`;
    	str += ` name="attachList[${index}].uuid" `;
    	str += ` value="${uuid}"/>`;
    	
    	str += ` <input type= "hidden"`;
    	str += ` name="attachList[${index}].uploadPath" `;
    	str += ` value="${path}"/>`;
    	
    });
    
//    f.innerHTML += str;
    f.insertAdjacentHTML("beforeend", str);
    
//    console.log(f);
    
    
    
    f.action = '/board/register';
    f.submit();

}










