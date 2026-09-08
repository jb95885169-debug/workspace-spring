document.querySelectorAll('.header a').forEach(a => {
	a.addEventListener('click', e=>{
		e.preventDefault();
		
		let menu = e.target.getAttribute('href');
		
		if(menu === 'mainPage'){
			location.href = '/';
		}else if(menu === 'boardList'){
			location.href = '/board/list';
		}
	});
});
document.querySelectorAll('.header-btn').forEach(a => {
	a.addEventListener('click', e=>{
		e.preventDefault();
		
		let menu = e.target.getAttribute('onclick');
		
		if(menu === 'loginPage()'){
			
			location.href = '/customLogin';
		}else if(menu === 'joinPage()'){
			console.log("회원가입 페이지로 ");
			location.href = '/joinMember';
		}
	});
});

function loginPage(){
	
}



// 전역에서 페이징을 사용하기 위한 함수 작성
function setStorageData(pageNum, amount){
	const pageData = {
		pageNum : pageNum,
		amount : amount
	};
	localStorage.setItem('page_data', JSON.stringify(pageData));
}
function getStorageData(){
	return JSON.parse( localStorage.getItem('page_data') );
}


// principal 객체 js로 가져오기
let principal;
async function getPrincipal(){
	try {
		const response = await fetch(`/api/currentUser.json`);
		const userPrincipal = await response.json();
		principal = userPrincipal.principal;
	} catch (e) {
		console.error("에러 : " + e );
	}
		
}
getPrincipal();








