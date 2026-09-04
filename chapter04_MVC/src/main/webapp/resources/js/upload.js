const regex = new RegExp("(.*?)\.(exe|sh|zip|alz)$");
const MAX_SIZE = 5242880; // 5MB

function checkExtension(fileName,fileSize){
	if(fileSize >= MAX_SIZE){
		alert('파일 사이즈 초과');
		return false;
	}
	if(regex.test(fileName)){
		alert("해당 종류의 파일은 업로드 할 수 없습니다.");
		return false;
	}
	return true;
}
// 비어있는 요소 복사해두기
// submit이 되어 파일이 업로드 되고 나면 초기화 진행 예정
let uploadDiv = document.querySelector(".uploadDiv");
let cloneObj = uploadDiv.firstElementChild.cloneNode(true);





document.querySelector('input[type="file"]').addEventListener('change', ()=>{
	const formData = new FormData();
	
	const inputFile = document.querySelector("input[type=file]");
	const files = inputFile.files;
	
	// file 객체들을 formData에 담기
	for(let i=0; i<files.length; i++){

		if(!checkExtension(files[i].name, files[i].size)){
			return false;
		}

		formData.append('uploadFile',files[i]);
	}
	
	// 담겨진 formData 전송
	fetch('/uploadAsyncAction',
			{
				method : 'post',
				body : formData
			}
		)
		.then(response => response.json())
		.then(result => {
			console.log(result);
			
			inputFile.value = '';
			showUploadedFile(result);
		})
		.catch(err => console.log(err));
});

// 전달 받은 파일 정보 화면 출력 함수
let uploadResult = document.querySelector(".uploadResult ul");
function showUploadedFile(uploadResultArr) {
	let str = ``;
	uploadResultArr.forEach( file =>{

		const {uploadPath, uuid, fileName} = file;	// 구조 분해 할당
		let fileCallPath = encodeURIComponent(`${file.uploadPath}/${uuid}_${fileName}`);
						// 
		str +=`<li path="${uploadPath}" uuid="${uuid}" fileName="${fileName}">`;
		str +=`<a>`;
	//	str +=`<a href="/download?fileName=${fileCallPath}">`;
		str +=`${file.fileName}`;
		str +=`</a>`;
		str +=`<span data-file="${fileCallPath}" > X </span>`;
		str +=`</li>`;
		
		
		// str += `<li>${file.fileName}</li>`;
	});
	uploadResult.innerHTML = str;
}

uploadResult.addEventListener('click', e=>{
	if(e.target.tagName === 'SPAN'){
		let targetFile = e.target.getAttribute('data-file');

		fetch("/deleteFile",{
			method: 'post',
			body : targetFile,
			headers : {
				'Content-type' : 'text/plain'
			}
			
		})
			.then(response => response.text())
			.then(result =>{
				let targetLi = e.target.closest('li');
				console.log(result);
				targetLi.remove();
			})
			.catch(err=>console.log(err));


	}
	
	
});























