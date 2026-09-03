# Smart Connect Hub

Build a modern, fully responsive React web application for a Smart Vehicle QR Code platform. The UI must strictly support Right-to-Left (RTL) direction for Arabic language and use a warm off-white background (#F9F8F6) with Coral/Red (#E53935) as the primary interactive brand color. Use Tailwind CSS, shadcn/ui components, and Lucide React for icons.

Layout Requirements:

- Sidebar (Right side in RTL): Contains navigation links (Overview, Contacts, QR Codes, Scan Logs, Messages, Notifications). Bottom of the sidebar should have Settings, Language Toggle, and a User Profile badge (e.g., "omar samir").

- Topbar: A simple search input field and page title.

- Main Content Area: Renders the active page with subtle borders and card-based layouts.

Core Pages to Implement:

1. Overview: A greeting header ("أهلاً omar"), a progress bar for 3 onboarding steps, and 3 metric cards (Scans, Incoming Messages, Active Contacts).

2. QR Customizer: A two-column layout. The right column has a form to select colors (Dots, Eyes, Background) and styles (Rounded, Square) using simple color pickers and icon buttons. The left column displays a live preview of the QR code using the 'qr-code-styling' logic.

3. Messages: A split-view layout. The right side lists conversation threads, and the left side is a blank state or chat view for the selected message.

4. Settings: A form to edit Full Name, Email, Password, and a distinct "Danger Zone" card at the bottom with a red button to delete the account.

5. Empty States: Provide clean empty states for Scan Logs and Notifications with subtle illustrations or icons.

This project was built with [Lovable](https://lovable.dev).

## Build with Lovable

Continue developing this project in the [Lovable editor](https://lovable.dev/projects/7316ae8d-2b4b-4be4-9c66-66bd0fccbc32).

- **Ship faster**: describe what you want to build and Lovable handles the code.
- **Stay in sync**: every change made in Lovable is committed straight to this repository.
- **Full ownership**: this code is yours. Push to `main` on GitHub and your changes sync back into Lovable, ready for your next prompt.

## Development

Prefer working locally? You need Node.js and npm — [install with nvm](https://github.com/nvm-sh/nvm#installing-and-updating).

```sh
git clone <this-repository-url>
cd <repository-name>
npm i
npm run dev
```
