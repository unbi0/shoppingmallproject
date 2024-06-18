export async function fetchWithAuth(url, options = {}) {
    let response = await fetch(url, options);

    // 만약 401 응답이면 /reissue API 호출
    if (response.status === 401) {
        const reissueResponse = await fetch('/reissue', {
            method: 'POST',
            credentials: 'include', // 필요한 경우
        });

        if (reissueResponse.ok) {
            // 재발급 후 원래 요청을 다시 시도
            response =  await fetch(url, options);
        } else {
            // 재발급 실패 시 에러 처리
            throw new Error('Token reissue failed');
        }
    }

    return response;
}