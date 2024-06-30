// document.addEventListener('DOMContentLoaded', function () {
//     var profileLink = document.querySelector('.profile-link');
//     var dropdownMenu = document.querySelector('.dropdown-menu');
//
//     profileLink.addEventListener('click', function (event) {
//         event.preventDefault();
//         dropdownMenu.classList.toggle('show');
//     });
//
//     window.addEventListener('click', function (event) {
//         if (!event.target.matches('.profile-link') && !event.target.closest('.dropdown-menu')) {
//             if (dropdownMenu.classList.contains('show')) {
//                 dropdownMenu.classList.remove('show');
//             }
//         }
//     });
// });

//header - dropdown 설정
function toggleDropdown() {
    const dropdownMenu = document.getElementById('dropdownMenu');
    dropdownMenu.style.display = dropdownMenu.style.display === 'block' ? 'none' : 'block';
}
// 페이지 외부 클릭 시 드롭다운 숨기기
window.onclick = function(event) {
    const dropdownMenu = document.getElementById('dropdownMenu');
    if (!event.target.matches('#profileImage') && dropdownMenu.style.display === 'block') {
        dropdownMenu.style.display = 'none';
    }
};

//로그아웃
function logOut() {
    fetch('/logout', {
        method: 'GET',
        credentials: 'same-origin'
    })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url; // 로그아웃 성공 시 리다이렉트
            } else {
                console.error('로그아웃 실패');
            }
        })
        .catch(error => console.error('로그아웃 중 오류 발생:', error));
}