export const createProject = async (payload) => {
  const response = await fetch(`${process.env.REACT_APP_API_URL}/projects`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  const data = await response.json();
  return data;
};
