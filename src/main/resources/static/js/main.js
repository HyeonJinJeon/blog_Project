// function getCookie(name) {
//     let cookieArr = document.cookie.split(";");
//     console.log(cookieArr[0]);
//     for (let i = 0; i < cookieArr.length; i++) {
//         let cookiePair = cookieArr[i].split("=");
//         console.log(cookiePair[0]);
//         if (name === cookiePair[0].trim()) {
//             return decodeURIComponent(cookiePair[1]);
//         }
//     }
//     return null;
// }
//
// function redirectToMyBlog() {
//     let username = getCookie("user");
//     // console.log(username);
//     if (username) {
//         window.location.href = `/my?username=${username}`;
//     } else {
//         alert("다시 로그인 해주세요!");
//     }
// }
// function redirectToMyBlog() {
//     fetch('/api/blogs/my', {
//         method: 'GET',
//         credentials: 'same-origin'  // 쿠키 전송을 위해 credentials 옵션 설정
//     })
//         .then(response => {
//             if (!response.ok) {
//                 throw new Error('Unauthorized');
//             }
//             return response.json();
//         })
//         .then(blog => {
//             // 성공적으로 블로그 정보를 받아온 경우, 사용자 이름을 확인하고 리다이렉트
//             let username = blog.user.username;  // 또는 필요한 정보로 대체
//             if (username) {
//                 window.location.href = `/my?username=${username}`;
//             } else {
//                 throw new Error('Invalid username');
//             }
//         })
//         .catch(error => {
//             console.error('Error fetching blog:', error);
//             alert('다시 로그인 해주세요!');  // 혹은 다른 처리 방법을 선택할 수 있음
//         });
// }

