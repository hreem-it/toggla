export const createProject = async (payload) => {
  const response = await fetch(`${process.env.REACT_APP_BE_API_URL}/projects`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (response.status === 400)
    throw new Error("Project with this project name already exists!");
};

export const createProjectKey = async (projectKey, payload) => {
  const response = await fetch(
    `${process.env.REACT_APP_BE_API_URL}/projects/${projectKey}/apikey`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    }
  );

  return response.text();
};

export const createToggle = async (apiSecret, payload) => {
  const response = await fetch(`${process.env.REACT_APP_BE_API_URL}/toggles/`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "api-secret": apiSecret,
    },
    body: JSON.stringify(payload),
  });

  if (response.status === 400)
    throw new Error("Toggle with this key already exists");
};

export const createToggleVariation = async (apiSecret, toggleKey, payload) => {
  const response = await fetch(
    `${process.env.REACT_APP_BE_API_URL}/toggles/${toggleKey}`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "api-secret": apiSecret,
      },
      body: JSON.stringify(payload),
    }
  );

  if (response.status === 400) throw new Error("Variation key already exists");
};

export const getProjects = async (_) => {
  const response = await fetch(`${process.env.REACT_APP_BE_API_URL}/projects`, {
    method: "GET",
    headers: { "Content-Type": "application/json" },
  });

  const data = await response.json();
  return data;
};

export const getToggles = async (apiSecret) => {
  const response = await fetch(`${process.env.REACT_APP_BE_API_URL}/toggles`, {
    method: "GET",
    headers: { "Content-Type": "application/json", "api-secret": apiSecret },
  });

  const data = await response.json();
  return data;
};

export const toggleToggle = async (apiSecret, toggleKey) => {
  await fetch(`${process.env.REACT_APP_BE_API_URL}/toggles/${toggleKey}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", "api-secret": apiSecret },
  });
};

export const toggleToggleVariant = async (apiSecret, toggleKey, variantKey) => {
  await fetch(
    `${process.env.REACT_APP_BE_API_URL}/toggles/${toggleKey}/${variantKey}`,
    {
      method: "PUT",
      headers: { "Content-Type": "application/json", "api-secret": apiSecret },
    }
  );
};

export const getProjectKeyFromApiKey = async (apiSecret) => {
  const response = await fetch(
    `${process.env.REACT_APP_BE_API_URL}/projects/verifykey`,
    {
      method: "GET",
      headers: { "Content-Type": "application/json", "api-secret": apiSecret },
    }
  );

  if (response.status === 403) {
    throw new Error("API Key is not valid for this project!");
  }

  return response.text();
};

export const getEnvironment = async (apiSecret, projectKey) => {
  const response = await fetch(
    `${process.env.REACT_APP_BE_API_URL}/projects/${projectKey}/environment`,
    {
      method: "GET",
      headers: { "Content-Type": "application/json", "api-secret": apiSecret },
    }
  );

  if (response.status === 403) {
    throw new Error("API Key is not valid for this project!");
  }

  return response.text();
};

export const getProject = async (apiSecret, projectKey) => {
  const response = await fetch(
    `${process.env.REACT_APP_BE_API_URL}/projects/${projectKey}`,
    {
      method: "GET",
      headers: { "Content-Type": "application/json", "api-secret": apiSecret },
    }
  );

  if (response.status === 403) {
    throw new Error("API Key is not valid for this project!");
  }

  return response.text();
};
