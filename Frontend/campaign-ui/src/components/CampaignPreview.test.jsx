// /**
//  * Tests for CampaignPreview component
//  *
//  * Property 3: Badge count matches customer IDs
//  * Validates: Requirements 4.4
//  *
//  * Property 4: Customer count summary reflects plan
//  * Validates: Requirements 4.5
//  *
//  * Unit tests: Requirements 4.1, 4.2, 4.6, 4.7, 4.8, 4.9, 5.1
//  */

// import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
// import { render, screen, fireEvent, waitFor, act } from '@testing-library/react'
// import { test } from '@fast-check/vitest'
// import * as fc from 'fast-check'
// import CampaignPreview from './CampaignPreview'

// // ============================================================
// // Helpers
// // ============================================================

// const makePlan = (customerIds = ['c1', 'c2'], subject = 'Hello', body = 'Body text') => ({
//     subject,
//     body,
//     customerIds,
// })

// const noop = () => { }

// // ============================================================
// // Property 3: Badge count matches customer IDs
// // Feature: cool-ui-redesign, Property 3: badge count matches customer IDs
// // Validates: Requirements 4.4
// // ============================================================

// describe('Feature: cool-ui-redesign, Property 3: badge count matches customer IDs', () => {
//     test.prop([
//         fc.array(fc.string(), { minLength: 0, maxLength: 50 }),
//         fc.string(),
//         fc.string(),
//     ])('badge count equals plan.customerIds.length', (customerIds, subject, body) => {
//         const plan = { customerIds, subject, body }
//         const { container } = render(
//             <CampaignPreview plan={plan} onReject={noop} addToast={noop} />
//         )
//         const badges = container.querySelectorAll('.badge')
//         expect(badges.length).toBe(customerIds.length)
//     })
// })

// // ============================================================
// // Property 4: Customer count summary reflects plan
// // Feature: cool-ui-redesign, Property 4: customer count summary reflects plan
// // Validates: Requirements 4.5
// // ============================================================

// describe('Feature: cool-ui-redesign, Property 4: customer count summary reflects plan', () => {
//     test.prop([
//         fc.array(fc.string(), { minLength: 0, maxLength: 50 }),
//         fc.string(),
//         fc.string(),
//     ])('customer count summary contains correct count and "customers"', (customerIds, subject, body) => {
//         const plan = { customerIds, subject, body }
//         const { container } = render(
//             <CampaignPreview plan={plan} onReject={noop} addToast={noop} />
//         )
//         const countEl = container.querySelector('.preview-customer-count')
//         expect(countEl).not.toBeNull()
//         expect(countEl.textContent).toContain(`${customerIds.length} customers`)
//     })
// })

// // ============================================================
// // Unit tests
// // ============================================================

// describe('CampaignPreview unit tests', () => {
//     let addToast
//     let onReject

//     beforeEach(() => {
//         addToast = vi.fn()
//         onReject = vi.fn()
//         vi.spyOn(window, 'alert').mockImplementation(() => { })
//     })

//     afterEach(() => {
//         vi.restoreAllMocks()
//     })

//     it('renders subject in a heading element (preview-subject)', () => {
//         render(<CampaignPreview plan={makePlan()} onReject={onReject} addToast={addToast} />)
//         const subjectEl = document.querySelector('.preview-subject')
//         expect(subjectEl).not.toBeNull()
//         expect(subjectEl.textContent).toBe('Hello')
//     })

//     it('Approve button has btn-success class', () => {
//         render(<CampaignPreview plan={makePlan()} onReject={onReject} addToast={addToast} />)
//         const approveBtn = screen.getByRole('button', { name: /approve/i })
//         expect(approveBtn.className).toContain('btn-success')
//     })

//     it('Reject button has btn-danger class', () => {
//         render(<CampaignPreview plan={makePlan()} onReject={onReject} addToast={addToast} />)
//         const rejectBtn = screen.getByRole('button', { name: /reject/i })
//         expect(rejectBtn.className).toContain('btn-danger')
//     })

//     it('shows spinner and "Sending..." on Approve click while fetch is pending', async () => {
//         let resolveFetch
//         global.fetch = vi.fn(() => new Promise((resolve) => { resolveFetch = resolve }))

//         render(<CampaignPreview plan={makePlan()} onReject={onReject} addToast={addToast} />)
//         const approveBtn = screen.getByRole('button', { name: /approve/i })
//         fireEvent.click(approveBtn)

//         await waitFor(() => {
//             expect(screen.getByText(/sending/i)).toBeInTheDocument()
//         })
//         expect(approveBtn.disabled).toBe(true)

//         // cleanup
//         resolveFetch({ ok: true, json: async () => ({ campaign_id: '123' }) })
//     })

//     it('calls addToast with success variant on successful send', async () => {
//         global.fetch = vi.fn(() =>
//             Promise.resolve({ ok: true, json: async () => ({ campaign_id: '123' }) })
//         )

//         render(<CampaignPreview plan={makePlan()} onReject={onReject} addToast={addToast} />)
//         fireEvent.click(screen.getByRole('button', { name: /approve/i }))

//         await waitFor(() => {
//             expect(addToast).toHaveBeenCalledWith(
//                 expect.objectContaining({ variant: 'success' })
//             )
//         })
//     })

//     it('calls addToast with error variant on failed send', async () => {
//         global.fetch = vi.fn(() => Promise.reject(new Error('Network error')))

//         render(<CampaignPreview plan={makePlan()} onReject={onReject} addToast={addToast} />)
//         fireEvent.click(screen.getByRole('button', { name: /approve/i }))

//         await waitFor(() => {
//             expect(addToast).toHaveBeenCalledWith(
//                 expect.objectContaining({ variant: 'error', message: 'Network error' })
//             )
//         })
//     })

//     it('does NOT call window.alert on successful send', async () => {
//         global.fetch = vi.fn(() =>
//             Promise.resolve({ ok: true, json: async () => ({ campaign_id: '123' }) })
//         )

//         render(<CampaignPreview plan={makePlan()} onReject={onReject} addToast={addToast} />)
//         fireEvent.click(screen.getByRole('button', { name: /approve/i }))

//         await waitFor(() => expect(addToast).toHaveBeenCalled())
//         expect(window.alert).not.toHaveBeenCalled()
//     })

//     it('card has card--animate-in class after mount', async () => {
//         const { container } = render(
//             <CampaignPreview plan={makePlan()} onReject={onReject} addToast={addToast} />
//         )
//         await waitFor(() => {
//             const card = container.querySelector('.card--preview')
//             expect(card.className).toContain('card--animate-in')
//         })
//     })

//     it('calls onReject when Reject button is clicked', () => {
//         render(<CampaignPreview plan={makePlan()} onReject={onReject} addToast={addToast} />)
//         fireEvent.click(screen.getByRole('button', { name: /reject/i }))
//         expect(onReject).toHaveBeenCalledTimes(1)
//     })
// })
